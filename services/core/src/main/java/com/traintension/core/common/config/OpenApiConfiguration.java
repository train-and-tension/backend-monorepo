package com.traintension.core.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;

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
                .description("Gateway (API Gateway üzerinden erişim)");

        Info info = new Info()
                .title("Train & Tension - Core Service API")
                .version("1.0.0")
                .description("""
                        Train & Tension uygulamasının Core servis API dokümantasyonu.
                        
                        ## Genel Bilgiler
                        - Tüm UUID alanları **UUIDv7** formatındadır.
                        - Tarih/saat alanları **ISO 8601** formatındadır (OffsetDateTime: `2024-01-15T10:30:00+03:00`).
                        - `version` alanı optimistic locking için kullanılır.
                        - Toplu işlemler (batch) `items` dizisi içinde gönderilir.
                        
                        ## Yetkilendirme
                        - **USER** rolü: Kişisel (personal) verilere erişim.
                        - **ADMIN** rolü: Global (system) verilere erişim ve yönetim.
                        
                        ## Hata Yanıtları
                        - `400` - Geçersiz istek (validation hatası)
                        - `401` - Yetkilendirme hatası (JWT eksik/geçersiz)
                        - `403` - Yetkisiz erişim (rol yetersiz)
                        - `404` - Kaynak bulunamadı
                        - `409` - Çakışma (optimistic lock / duplicate)
                        """)
                .contact(new Contact()
                        .name("Train & Tension Team")
                        .email("dev@traintension.com"));

        return new OpenAPI()
                .info(info)
                .servers(List.of(gateway))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .name(BEARER_AUTH)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Gateway JWT token. Token girin, gateway otomatik olarak X-User-Id / X-User-Roles header'larını inject edecektir.")));
    }

    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("1-admin")
                .displayName("Admin API")
                .packagesToScan("com.traintension.core")
                .pathsToMatch("/**/admin/**")
                .build();
    }

    @Bean
    public GroupedOpenApi userApi() {
        return GroupedOpenApi.builder()
                .group("2-user")
                .displayName("User API")
                .packagesToScan("com.traintension.core")
                .pathsToExclude("/**/admin/**", "/**/inline/**")
                .build();
    }

    @Bean
    public OpenApiCustomizer tagJsonDownloadLinks() {
        Map<String, String> tagGroups = Map.ofEntries(
                Map.entry("Body Information", "model-body-information"),
                Map.entry("Equipment", "model-equipment"),
                Map.entry("Exercise", "model-exercise"),
                Map.entry("Exercise Muscle", "model-exercise-muscle"),
                Map.entry("Saved Exercise", "model-saved-exercise"),
                Map.entry("Set Result", "model-set-result"),
                Map.entry("Sync", "model-sync"),
                Map.entry("Target Set", "model-target-set"),
                Map.entry("User Profile", "model-user-profile"),
                Map.entry("Workout Day", "model-workout-day"),
                Map.entry("Workout Program", "model-workout-program"),
                Map.entry("Workout Session", "model-workout-session")
        );

        return openApi -> {
            if (openApi.getTags() == null) {
                return;
            }

            openApi.getTags().forEach(tag -> addJsonDownloadLink(tag, tagGroups));
        };
    }

    @Bean
    public GroupedOpenApi exerciseModelApi() {
        return modelApi("model-exercise", "Exercise Model", ApiConstants.EXERCISE, ApiConstants.ADMIN_EXERCISE);
    }

    @Bean
    public GroupedOpenApi equipmentModelApi() {
        return modelApi("model-equipment", "Equipment Model", ApiConstants.EQUIPMENT, ApiConstants.ADMIN_EQUIPMENT);
    }

    @Bean
    public GroupedOpenApi exerciseMuscleModelApi() {
        return modelApi("model-exercise-muscle", "Exercise Muscle Model",
                ApiConstants.EXERCISE_MUSCLE, ApiConstants.ADMIN_EXERCISE_MUSCLE);
    }

    @Bean
    public GroupedOpenApi muscleModelApi() {
        return modelApi("model-muscle", "Muscle Model", ApiConstants.MUSCLE, ApiConstants.ADMIN_MUSCLE);
    }

    @Bean
    public GroupedOpenApi workoutProgramModelApi() {
        return modelApi("model-workout-program", "Workout Program Model",
                ApiConstants.WORKOUT_PROGRAM, ApiConstants.ADMIN_WORKOUT_PROGRAM);
    }

    @Bean
    public GroupedOpenApi workoutDayModelApi() {
        return modelApi("model-workout-day", "Workout Day Model",
                ApiConstants.WORKOUT_DAY, ApiConstants.ADMIN_WORKOUT_DAY);
    }

    @Bean
    public GroupedOpenApi workoutSessionModelApi() {
        return modelApi("model-workout-session", "Workout Session Model", ApiConstants.WORKOUT_SESSION);
    }

    @Bean
    public GroupedOpenApi syncModelApi() {
        return modelApi("model-sync", "Sync Model", ApiConstants.SYNC);
    }

    @Bean
    public GroupedOpenApi targetSetModelApi() {
        return modelApi("model-target-set", "Target Set Model",
                ApiConstants.TARGET_SET, ApiConstants.ADMIN_TARGET_SET);
    }

    @Bean
    public GroupedOpenApi setResultModelApi() {
        return modelApi("model-set-result", "Set Result Model", ApiConstants.SET_RESULT);
    }

    @Bean
    public GroupedOpenApi bodyInformationModelApi() {
        return modelApi("model-body-information", "Body Information Model", ApiConstants.BODY_INFORMATION);
    }

    @Bean
    public GroupedOpenApi userProfileModelApi() {
        return modelApi("model-user-profile", "User Profile Model",
                ApiConstants.USER_PROFILE, ApiConstants.ADMIN_USER_PROFILE);
    }

    @Bean
    public GroupedOpenApi savedExerciseModelApi() {
        return modelApi("model-saved-exercise", "Saved Exercise Model", ApiConstants.SAVED_EXERCISE);
    }

    private GroupedOpenApi modelApi(String group, String displayName, String... basePaths) {
        return GroupedOpenApi.builder()
                .group(group)
                .displayName(displayName)
                .packagesToScan("com.traintension.core")
                .pathsToMatch(expandBasePaths(basePaths))
                .build();
    }

    private String[] expandBasePaths(String... basePaths) {
        String[] pathsToMatch = new String[basePaths.length * 2];

        for (int i = 0; i < basePaths.length; i++) {
            pathsToMatch[i * 2] = basePaths[i];
            pathsToMatch[i * 2 + 1] = basePaths[i] + "/**";
        }

        return pathsToMatch;
    }

    private void addJsonDownloadLink(Tag tag, Map<String, String> tagGroups) {
        String group = tagGroups.get(tag.getName());

        if (group == null) {
            return;
        }

        String downloadLink = "[JSON dokumanini indir](/swagger/core/v3/api-docs/" + group + ")";
        String description = tag.getDescription();

        if (description == null || description.isBlank()) {
            tag.setDescription(downloadLink);
            return;
        }

        if (!description.contains(downloadLink)) {
            tag.setDescription(description + "\n\n" + downloadLink);
        }
    }
}
