package com.traintension.identity.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnProperty(name = "SWAGGER_ENABLED", havingValue = "true", matchIfMissing = false)
public class OpenApiConfiguration {

    private static final String BEARER_AUTH = "bearerAuth";

    @Value("${GATEWAY_PORT}")
    private int gatewayPort;

    @Bean
    public OpenAPI openAPI() {
        Server gateway = new Server()
                .url("/")
                .description("Gateway");

        return new OpenAPI()
                .servers(List.of(gateway))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Gateway JWT token. Enter your token and gateway will inject X-User-Id / X-User-Roles headers automatically.")));
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .packagesToScan("com.traintension.identity")
                .pathsToMatch("/**/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("user")
                .packagesToScan("com.traintension.identity")
                .pathsToExclude("/**/admin/**")
                .build();
    }
}
