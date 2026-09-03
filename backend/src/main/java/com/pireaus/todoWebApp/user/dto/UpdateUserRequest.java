package com.pireaus.todoWebApp.user.dto;

import com.pireaus.todoWebApp.user.User;

/**
 * fullName/email are always applied by whoever is allowed to update this
 * user (self or admin). type/status/password are admin-only fields and the
 * service leaves them untouched when null - so a partial payload (or a
 * non-admin caller) can never silently wipe a role, disable an account, or
 * blank out a password.
 */
public record UpdateUserRequest(
        String fullName,
        String email,
        User.UserCategory type,
        Boolean status,
        String password
) {
}
