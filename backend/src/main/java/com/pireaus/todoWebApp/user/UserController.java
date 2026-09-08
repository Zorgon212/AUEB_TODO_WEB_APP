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

    private URI locationOf(Integer id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/users/{id}")
                .buildAndExpand(id)
                .toUri();
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

    @Operation(summary = "create a user with (admin only)")
    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@RequestBody CreateUserRequest request) {
        UserResponse saved = userService.createByAdmin(request);
        return ResponseEntity.created(locationOf(saved.id())).build();
    }

    @Operation(summary = "Fetch user by id")
    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponse> retrieveUser(@PathVariable Integer id){
        return ResponseEntity.ok(userService.findById(id));
    }

    // cascade deleting the tasks also
    @Operation(summary = "delete a user (also deletes all the todos that belonged to that user) (only for admin)")
    @DeleteMapping("/users/{id}")
    public void deleteClient(@PathVariable Integer id, Authentication authentication){
        userService.delete(id, authentication.getName());
    }

    // an admin can also update other users
    @Operation(summary = "Update a user by id")
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateClient(
            @PathVariable Integer id,
            @RequestBody UpdateUserRequest request,
            Authentication authentication
    ) {
        UserResponse updated = userService.update(id, request, authentication.getName());
        return ResponseEntity.ok().location(locationOf(updated.id())).body(updated);
    }
}
