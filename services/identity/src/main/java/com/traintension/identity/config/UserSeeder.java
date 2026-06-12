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
public class UserSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final UserProfileClient userProfileClient;

    @Value("${app.user.emails}")
    private String userEmails;

    @Override
    public void run(ApplicationArguments args) {
        if (userEmails == null || userEmails.isBlank()) {
            return;
        }

        List<String> emails = Arrays.stream(userEmails.split(","))
                .map(String::trim)
                .filter(e -> !e.isBlank())
                .toList();

        for (String email : emails) {
            userRepository.findByEmail(email).ifPresentOrElse(
                    existing -> {
                        if (!existing.getRoles().contains(UserRole.USER)) {
                            existing.setRoles(List.of(UserRole.USER));
                            userRepository.save(existing);
                            log.info("User roles updated: {}", email);
                        } else {
                            log.info("User already seeded, skipping role update: {}", email);
                        }
                        createUserProfileIfNotExists(existing.getId(), email);
                    },
                    () -> {
                        User user = User.builder()
                                .id(UuidCreator.getTimeOrderedEpoch())
                                .email(email)
                                .roles(List.of(UserRole.USER))
                                .createdAt(Instant.now())
                                .isDelete(false)
                                .build();
                        userRepository.save(user);
                        log.info("User seeded: {}", email);
                        createUserProfileIfNotExists(user.getId(), email);
                    }
            );
        }
    }

    private void createUserProfileIfNotExists(UUID userId, String email) {
        if (Boolean.TRUE.equals(userProfileClient.existsByUserId(userId))) {
            log.info("UserProfile already exists for userId: {}. Skipping.", userId);
            return;
        }

        String firstName = email.split("@")[0];

        CreateUserProfileRequest request = CreateUserProfileRequest.builder()
                .userId(userId)
                .firstName(firstName)
                .lastName("User")
                .build();

        userProfileClient.createUserProfile(request);
        log.info("UserProfile created for userId: {}", userId);
    }
}
