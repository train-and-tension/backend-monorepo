package com.traintension.common.utils.user;

import com.traintension.common.exception.custom.InternalServerErrorException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserContext {

    private static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes()
        )).getRequest();
    }

    public static boolean isAdmin() {
        return getRoles().contains("ADMIN");
    }

    public static List<String> getRoles() {
        String roles = getRequest().getHeader(UserHeaders.USER_ROLES);

        if (roles == null || roles.isBlank()) {
            return List.of();
        }

        return Arrays.stream(roles.split(","))
                .map(String::trim)
                .map(String::toUpperCase)
                .toList();
    }

    public static UUID getId() {
        String userId = getRequest().getHeader(UserHeaders.USER_ID);

        if (userId == null || userId.isBlank()) {
            throw new InternalServerErrorException("User ID is missing in context.");
        }

        return UUID.fromString(userId);
    }

    public static String getEmail() {
        String userEmail = getRequest().getHeader(UserHeaders.USER_EMAIL);

        if (userEmail == null || userEmail.isBlank()) {
            throw new InternalServerErrorException("User Email is missing in context.");
        }

        return userEmail;
    }

    public static String getRequestId() {
        return getRequest().getHeader(UserHeaders.USER_REQUEST_ID);
    }

}