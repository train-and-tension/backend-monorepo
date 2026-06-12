package com.traintension.identity.services;

import com.github.f4b6a3.uuid.UuidCreator;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.traintension.common.dto.CreateUserProfileRequest;
import com.traintension.common.exception.custom.ForbiddenException;
import com.traintension.common.exception.custom.UnauthorizedException;
import com.traintension.common.utils.user.UserRole;
import com.traintension.identity.config.interfaces.UserProfileClient;
import com.traintension.identity.dto.GoogleTokenRequestDTO;
import com.traintension.identity.dto.TokenPairDTO;
import com.traintension.identity.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserService userService;
    private final GoogleTokenVerifierService googleVerifierService;
    private final TokenPairService tokenPairService;
    private final RefreshTokenService refreshTokenService;
    private final UserProfileClient userProfileClient;

    public TokenPairDTO googleAuth(GoogleTokenRequestDTO dto) {
        GoogleIdToken.Payload payload = googleVerifierService.verify(dto.idToken());

        String email = payload.getEmail();
        String googleId = payload.getSubject();

        Boolean isVerifiedEmail = payload.getEmailVerified();

        if (!isVerifiedEmail) {
            throw new UnauthorizedException();
        }

        User user = userService.findByEmail(email)
                .map(existingUser -> {
                    if (Boolean.TRUE.equals(existingUser.getIsDelete())) {
                        throw new ForbiddenException();
                    }
                    existingUser.setGoogleId(googleId);
                    return existingUser;
                })
                .orElse(null);

        if (user != null) {
            User savedUser = userService.save(user);
            return tokenPairService.generate(savedUser);
        }

        UUID newUserId = UuidCreator.getTimeOrderedEpoch();

        createUserProfile(payload, newUserId);

        User newUser = User.builder()
                .id(newUserId)
                .googleId(googleId)
                .email(email)
                .roles(List.of(UserRole.USER))
                .createdAt(Instant.now())
                .isDelete(false)
                .build();

        User savedUser = userService.save(newUser);

        return login(savedUser);
    }

    private void createUserProfile(GoogleIdToken.Payload payload, UUID userId) {
        userProfileClient.createUserProfile(
                CreateUserProfileRequest.builder()
                        .userId(userId)
                        .firstName((String) payload.get("given_name"))
                        .lastName((String) payload.get("family_name"))
                        .profilePicUrl((String) payload.get("picture"))
                        .build()
        );
    }


    public TokenPairDTO refresh(String refreshToken) {
        UUID userId = refreshTokenService.getUserId(refreshToken);
        refreshTokenService.revoke(refreshToken);

        User user = userService.findById(userId).orElseThrow(UnauthorizedException::new);

        return tokenPairService.generate(user);
    }

    public void logout(String refreshToken) {
        refreshTokenService.revoke(refreshToken);
    }

    public void logoutAll(UUID userId) {
        refreshTokenService.revokeAll(userId);
    }

    private TokenPairDTO login(User user) {
        logoutAll(user.getId());
        return tokenPairService.generate(user);
    }
}