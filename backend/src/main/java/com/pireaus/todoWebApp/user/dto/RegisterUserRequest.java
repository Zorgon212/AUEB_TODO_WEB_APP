package com.pireaus.todoWebApp.user.dto;

// self-registration payload - always becomes a plain USER, see CreateUserRequest for admin-created accounts
public record RegisterUserRequest(
        String fullName,
        String email,
        String password
) {
}
