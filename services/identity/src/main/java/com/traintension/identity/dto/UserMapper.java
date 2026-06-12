package com.traintension.identity.dto;

import com.traintension.identity.models.User;

public class UserMapper {
    public static UserDTO.ActiveUserResponse toActiveResponse(User user) {
        return UserDTO.ActiveUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .build();
    }

    public static UserDTO.InactiveUserResponse toInactiveResponse(User user) {
        return UserDTO.InactiveUserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }

    public static UserDTO.UserResponse toResponse(User user) {
        return UserDTO.UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .roles(user.getRoles())
                .createdAt(user.getCreatedAt())
                .deletedAt(user.getDeletedAt())
                .build();
    }
}
