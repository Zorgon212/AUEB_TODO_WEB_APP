package com.pireaus.todoWebApp.user.dto;

import com.pireaus.todoWebApp.user.User;

// admin-only: creating a user with a chosen role
public record CreateUserRequest(
        String fullName,
        String email,
        String password,
        User.UserCategory type
) {
}
