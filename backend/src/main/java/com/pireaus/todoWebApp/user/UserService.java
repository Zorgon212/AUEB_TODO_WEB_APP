package com.pireaus.todoWebApp.user;

import com.pireaus.todoWebApp.common.exception.EmailAlreadyExistsException;
import com.pireaus.todoWebApp.common.exception.NotFoundException;
import com.pireaus.todoWebApp.user.dto.CreateUserRequest;
import com.pireaus.todoWebApp.user.dto.RegisterUserRequest;
import com.pireaus.todoWebApp.user.dto.UpdateUserRequest;
import com.pireaus.todoWebApp.user.dto.UserResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

// application service: orchestrates the User aggregate + enforces who is
// allowed to do what. Controllers stay thin HTTP adapters that just call in here.
@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepo userRepo,
            PasswordEncoder passwordEncoder) {

        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> findAll() {
        return userRepo.findAll().stream().map(UserResponse::from).toList();
    }

    public UserResponse findById(Integer id) {
        return UserResponse.from(getOrThrow(id));
    }

    public UserResponse findCurrent(String email) {
        return UserResponse.from(getByEmailOrThrow(email));
    }

    // self-registration - always a plain USER
    public UserResponse register(RegisterUserRequest request) {
        assertEmailAvailable(request.email());

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.changePassword(passwordEncoder.encode(request.password()));
        user.promoteTo(User.UserCategory.USER);
        user.setStatus(true);

        return UserResponse.from(userRepo.save(user));
    }

    // admin-initiated creation - the admin may choose the role
    public UserResponse createByAdmin(CreateUserRequest request) {
        assertEmailAvailable(request.email());

        User user = new User();
        user.setFullName(request.fullName());
        user.setEmail(request.email());
        user.changePassword(passwordEncoder.encode(request.password()));
        user.promoteTo(request.type() != null ? request.type() : User.UserCategory.USER);
        user.setStatus(true);

        return UserResponse.from(userRepo.save(user));
    }

    // a user may update their own info; an admin may update anyone's.
    // type/status/password are only ever touched when present AND the
    // acting user is an admin - see UpdateUserRequest's contract.
    public UserResponse update(Integer targetId, UpdateUserRequest request, String actingUserEmail) {
        User current = getByEmailOrThrow(actingUserEmail);
        requireSelfOrAdmin(current, targetId);

        User existing = getOrThrow(targetId);
        existing.setFullName(request.fullName());
        existing.setEmail(request.email());

        if (current.isAdmin()) {
            if (request.type() != null) {
                existing.promoteTo(request.type());
            }
            if (request.status() != null) {
                existing.setStatus(request.status());
            }
            if (request.password() != null && !request.password().isBlank()) {
                existing.changePassword(passwordEncoder.encode(request.password()));
            }
        }

        return UserResponse.from(userRepo.save(existing));
    }

    // admin-only - deleting a user also deletes their todos (cascade on User.todos)
    public void delete(Integer targetId, String actingUserEmail) {
        User current = getByEmailOrThrow(actingUserEmail);
        if (!current.isAdmin()) {
            throw new AccessDeniedException("Only an admin can delete a user");
        }
        if (!userRepo.existsById(targetId)) {
            throw new NotFoundException("User not found with id: " + targetId);
        }
        userRepo.deleteById(targetId);
    }

    // --- shared lookups / guards --------------------------------------------

    User getByEmailOrThrow(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));
    }

    private User getOrThrow(Integer id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
    }

    private void assertEmailAvailable(String email) {
        if (userRepo.findByEmail(email).isPresent()) {
            throw new EmailAlreadyExistsException(
                    "This email is already registered: " + email);
        }
    }

    private void requireSelfOrAdmin(User current, Integer targetId) {
        if (!current.isAdmin() && !current.isSameUserAs(targetId)) {
            throw new AccessDeniedException("You are not allowed to modify this user");
        }
    }
}
