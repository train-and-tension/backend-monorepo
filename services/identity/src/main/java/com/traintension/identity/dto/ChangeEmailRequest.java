package com.traintension.identity.dto;

import com.traintension.identity.constants.Provider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChangeEmailRequest(
        @NotNull
        Provider provider,

        @NotBlank
        String idToken
) {}

