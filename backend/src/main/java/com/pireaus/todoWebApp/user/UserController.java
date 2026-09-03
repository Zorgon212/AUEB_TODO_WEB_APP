package com.pireaus.todoWebApp.user;

import com.pireaus.todoWebApp.user.dto.CreateUserRequest;
import com.pireaus.todoWebApp.user.dto.RegisterUserRequest;
import com.pireaus.todoWebApp.user.dto.UpdateUserRequest;
import com.pireaus.todoWebApp.user.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * Remember that the login controller was not added because spring security has a default login api call
 * at POST /login
 * with form parameters username, password
 * */
@RestController
@Tag(name = "Users", description = "Registration, admin user management, and the current session's own profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "List all users (admin only)")
    @GetMapping("/users")
    public List<UserResponse> retrieveAllClients(){
        return userService.findAll();
    }

    // the currently logged in user - used by the frontend to know who's signed in
    @Operation(summary = "The currently logged-in user")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(userService.findCurrent(authentication.getName()));
    }

    // self-registration - always creates a plain USER (see /users below for admin-created accounts)
    @Operation(summary = "Self-registration - always creates a plain USER")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterUserRequest request) {
        UserResponse saved = userService.register(request);
        return ResponseEntity.created(locationOf(saved.id())).build();
    }

    // admin-only: create a user with a chosen role (USER or ADMIN) - enforced by
    // SecurityConfig's exact-path "/users" -> hasRole("ADMIN") rule
    @Operation(summary = "Admin-only: create a user with a chosen role")
    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@RequestBody CreateUserRequest request) {
        UserResponse saved = userService.createByAdmin(request);
        return ResponseEntity.created(locationOf(saved.id())).build();
    }

    @Operation(summary = "Fetch a single user by id")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> retrieveUser(@PathVariable Integer id){
        return ResponseEntity.ok(userService.findById(id));
    }

    // admin-only - deleting a user also deletes their todos (cascade on User.todos)
    @Operation(summary = "Admin-only: delete a user (cascades to their todos)")
    @DeleteMapping("/users/{id}")
    public void deleteClient(@PathVariable Integer id, Authentication authentication){
        userService.delete(id, authentication.getName());
    }

    // a user may update their own info; an admin may update anyone's.
    @Operation(summary = "Update a user - self or admin; role/status/password changes require admin")
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateClient(
            @PathVariable Integer id,
            @RequestBody UpdateUserRequest request,
            Authentication authentication
    ) {
        UserResponse updated = userService.update(id, request, authentication.getName());
        return ResponseEntity.ok().location(locationOf(updated.id())).body(updated);
    }

    private URI locationOf(Integer id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/users/{id}")
                .buildAndExpand(id)
                .toUri();
    }
}
