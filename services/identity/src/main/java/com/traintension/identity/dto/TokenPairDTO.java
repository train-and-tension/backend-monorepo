package com.traintension.identity.dto;

public record TokenPairDTO(
        String accessToken,
        String refreshToken
) {
}
