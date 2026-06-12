package com.traintension.common.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.UUID;

@Builder
public record CreateUserProfileRequest(
        @NotNull
        UUID userId,
        String firstName,
        String lastName,
        String profilePicUrl
) {
}
