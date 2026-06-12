package com.traintension.identity.config;

import com.github.f4b6a3.uuid.UuidCreator;
import com.traintension.common.dto.CreateUserProfileRequest;
import com.traintension.common.utils.user.UserRole;
import com.traintension.identity.config.interfaces.UserProfileClient;
import com.traintension.identity.models.User;
import com.traintension.identity.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final UserProfileClient userProfileClient;

    @Value("${app.admin.emails}")
    private String adminEmails;

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmails == null || adminEmails.isBlank()) {
            return;
        }

        List<String> emails = Arrays.stream(adminEmails.split(","))
                .map(String::trim)
                .filter(e -> !e.isBlank())
                .toList();

        for (String email : emails) {
            userRepository.findByEmail(email).ifPresentOrElse(
                    existing -> {
                        if (!existing.getRoles().contains(UserRole.ADMIN)) {
                            existing.setRoles(List.of(UserRole.ADMIN));
                            userRepository.save(existing);
                            log.info("Admin roles updated: {}", email);
                        } else {
                            log.info("Admin already seeded, skipping role update: {}", email);
                        }
                        createUserProfileIfNotExists(existing.getId(), email);
                    },
                    () -> {
                        User admin = User.builder()
                                .id(UuidCreator.getTimeOrderedEpoch())
                                .email(email)
                                .roles(List.of(UserRole.ADMIN))
                                .createdAt(Instant.now())
                                .isDelete(false)
                                .build();
                        userRepository.save(admin);
                        log.info("Admin seeded: {}", email);
                        createUserProfileIfNotExists(admin.getId(), email);
                    }
            );
        }
    }

    private void createUserProfileIfNotExists(UUID userId, String email) {
        if (Boolean.TRUE.equals(userProfileClient.existsByUserId(userId))) {
            log.info("UserProfile already exists for admin userId: {}. Skipping.", userId);
            return;
        }

        String firstName = email.split("@")[0];

        CreateUserProfileRequest request = CreateUserProfileRequest.builder()
                .userId(userId)
                .firstName(firstName)
                .lastName("Admin")
                .build();

        userProfileClient.createUserProfile(request);
        log.info("UserProfile created for admin userId: {}", userId);
    }
}
