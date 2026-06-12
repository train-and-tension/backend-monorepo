package com.traintension.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record GoogleTokenRequestDTO(
        @NotBlank
        String idToken
) {
}
