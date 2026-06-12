package com.traintension.common.utils.user;

import java.util.List;
import java.util.UUID;

public record UserPrincipal(
        UUID id,
        String email,
        List<UserRole> roles,
        String requestId
) {
}

