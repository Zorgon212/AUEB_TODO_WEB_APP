package com.pireaus.todoWebApp.user.dto;

import com.pireaus.todoWebApp.user.User;

// No password
public record UserResponse(
        Integer id,
        String fullName,
        String email,
        boolean status,
        User.UserCategory type
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.isStatus(),
                user.getType()
        );
    }
}
