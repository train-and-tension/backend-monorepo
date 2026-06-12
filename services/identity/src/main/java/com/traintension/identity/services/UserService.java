package com.traintension.identity.services;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.traintension.common.dto.CreateUserProfileRequest;
import com.traintension.common.exception.custom.BadRequestException;
import com.traintension.common.exception.custom.ConflictException;
import com.traintension.common.exception.custom.NotFoundException;
import com.traintension.common.utils.user.UserRole;
import com.traintension.identity.config.interfaces.UserProfileClient;
import com.traintension.identity.constants.Provider;
import com.traintension.identity.dto.ChangeEmailRequest;
import com.traintension.identity.dto.TokenPairDTO;
import com.traintension.identity.dto.UserDTO.*;
import com.traintension.identity.dto.UserMapper;
import com.traintension.identity.models.User;
import com.traintension.identity.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository rep;

    private final GoogleTokenVerifierService googleVerifierService;
    private final RefreshTokenService refreshTokenService;
    private final TokenPairService tokenPairService;
    private final UserProfileClient userProfileClient;

    public TokenPairDTO changeEmail(UUID userId, String currentEmail, ChangeEmailRequest dto) {
        if (dto.provider() == Provider.GOOGLE) {
            return changeEmailViaGoogle(userId, currentEmail, dto.idToken());
        }
        throw new BadRequestException();
    }

    private TokenPairDTO changeEmailViaGoogle(UUID userId, String currentEmail, String idToken) {
        GoogleIdToken.Payload payload = googleVerifierService.verify(idToken);

        String newEmail = payload.getEmail();
        String googleId = payload.getSubject();

        if (currentEmail.equals(newEmail)) {
            throw new ConflictException();
        }

        if (rep.existsByEmail(newEmail)) {
            throw new ConflictException();
        }

        User user = rep.findByEmail(currentEmail).orElseThrow(NotFoundException::new);

        user.setEmail(newEmail);
        user.setGoogleId(googleId);

        rep.save(user);

        refreshTokenService.revokeAll(userId);
        return tokenPairService.generate(user);
    }

    public Optional<User> findByEmail(String email) {
        return rep.findByEmail(email);
    }


    public Optional<User> findById(UUID id) {
        return rep.findById(id);
    }

    public User save(User user) {
        return rep.save(user);
    }

    public Slice<ActiveUserResponse> getPaged(int page) {
        return rep.findAllByIsDeleteFalse(PageRequest.of(page, 500, Sort.by("createdAt").descending()))
                .map(UserMapper::toActiveResponse);
    }

    public Slice<InactiveUserResponse> getDeletedPaged(int page) {
        return rep.findAllByIsDeleteTrue(PageRequest.of(page, 500, Sort.by("createdAt").descending()))
                .map(UserMapper::toInactiveResponse);
    }

    public void deactivateUser(UUID id) {
        User user = rep.findById(id)
                .orElseThrow(NotFoundException::new);

        if(user.getIsDelete()) {
            return;
        }

        userProfileClient.deleteUserProfile(id);
        refreshTokenService.revokeAll(id);

        user.setRoles(Collections.emptyList());
        user.setIsDelete(true);
        user.setDeletedAt(Instant.now());
        rep.save(user);
    }

    public void restoreUser(UUID id) {
        User user = rep.findById(id)
                .orElseThrow(NotFoundException::new);

        if(!user.getIsDelete()) {
            return;
        }

        userProfileClient.deleteUserProfile(id);
        userProfileClient.createUserProfile(
                CreateUserProfileRequest.builder().userId(id).build()
        );

        user.setIsDelete(false);
        user.setDeletedAt(null);
        user.setRoles(Collections.singletonList(UserRole.USER));
        rep.save(user);
    }

    public Slice<UserResponse> searchByEmail(String query, int page) {
        return rep.findByEmailRegex(query, PageRequest.of(page, 50))
                .map(UserMapper::toResponse);
    }


    public UserResponse getByEmail(String email) {
        return rep.findByEmail(email)
                .map(UserMapper::toResponse)
                .orElseThrow(NotFoundException::new);
    }

    public UserResponse getById(UUID id) {
        return rep.findById(id)
                .map(UserMapper::toResponse)
                .orElseThrow(NotFoundException::new);
    }

}
