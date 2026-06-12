package com.traintension.gateway.filters;

import com.nimbusds.jwt.JWTClaimsSet;
import com.traintension.common.utils.user.UserHeaders;
import com.traintension.common.utils.user.UserRole;
import com.traintension.gateway.services.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {
    private final JwtService jwtService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        boolean hasJwt = authHeader != null && authHeader.startsWith("Bearer ");

        if (!hasJwt) {
            return chain.filter(exchange.mutate().request(anonymousRequest(request)).build());
        }

        try {
            JWTClaimsSet claims = jwtService.verify(authHeader.substring(7));
            return chain.filter(exchange.mutate().request(authenticatedRequest(request, claims)).build());
        } catch (Exception e) {
            log.warn("JWT Verification failed: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private ServerHttpRequest anonymousRequest(ServerHttpRequest request) {
        return request.mutate()
                .headers(h -> h.remove(UserHeaders.USER_ROLES))
                .header(UserHeaders.USER_ROLES, UserRole.ANONYMOUS.name())
                .header(UserHeaders.USER_REQUEST_ID, UUID.randomUUID().toString().substring(0, 8))
                .build();
    }

    private ServerHttpRequest authenticatedRequest(ServerHttpRequest request, JWTClaimsSet claims) throws Exception {
        return request.mutate()
                .headers(h -> {
                    h.remove(UserHeaders.USER_ID);
                    h.remove(UserHeaders.USER_EMAIL);
                    h.remove(UserHeaders.USER_ROLES);
                    h.remove(UserHeaders.USER_REQUEST_ID);
                })
                .header(UserHeaders.USER_ID, claims.getSubject())
                .header(UserHeaders.USER_EMAIL, claims.getStringClaim("email"))
                .header(UserHeaders.USER_ROLES, String.join(",", claims.getStringListClaim("roles")))
                .header(UserHeaders.USER_REQUEST_ID, UUID.randomUUID().toString().substring(0, 8))
                .build();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}