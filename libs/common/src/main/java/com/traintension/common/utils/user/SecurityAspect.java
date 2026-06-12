package com.traintension.common.utils.user;

import com.traintension.common.exception.custom.UnauthorizedException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Aspect
@Component
public class SecurityAspect {

    private final HttpServletRequest request;

    public SecurityAspect(HttpServletRequest request) {
        this.request = request;
    }

    @Before("@annotation(requiredRole)")
    public void checkAccessOnMethod(RequiredRole requiredRole) {
        checkAccess(requiredRole);
    }

    @Before("@within(requiredRole) && !@annotation(com.traintension.common.utils.user.RequiredRole)")
    public void checkAccessOnClass(RequiredRole requiredRole) {
        checkAccess(requiredRole);
    }

    private void checkAccess(RequiredRole requiredRole) {
        List<String> requiredRoles = Arrays.asList(requiredRole.value());

        String header = request.getHeader(UserHeaders.USER_ROLES);

        if (header == null || header.isBlank()) {
            throw new UnauthorizedException();
        }

        Set<String> userRoles = Arrays.stream(header.split(","))
                .map(role -> role.trim().toUpperCase())
                .collect(Collectors.toSet());

        boolean hasAccess = requiredRoles.stream()
                .map(String::toUpperCase)
                .anyMatch(userRoles::contains);

        if (!hasAccess) {
            throw new UnauthorizedException();
        }
    }
}