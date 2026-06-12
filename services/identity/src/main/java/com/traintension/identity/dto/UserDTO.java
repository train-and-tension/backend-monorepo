package com.traintension.identity.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.traintension.common.utils.user.UserRole;
import lombok.Builder;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


public class UserDTO {

    @Builder
    public record ActiveUserResponse(
            UUID id,
            String email,
            List<UserRole> roles,
            Instant createdAt
    ) {
    }

    @Builder
    public record InactiveUserResponse(
            UUID id,
            String email,
            Instant createdAt,
            Instant deletedAt
    ) {
    }

    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UserResponse(
            UUID id,
            String email,
            List<UserRole> roles,
            Instant createdAt,
            Instant deletedAt
    ) {
    }
}

