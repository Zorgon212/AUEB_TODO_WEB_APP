package com.pireaus.todoWebApp.controllers;

import com.pireaus.todoWebApp.entities.User;
import com.pireaus.todoWebApp.repo.UserRepo;
import com.pireaus.todoWebApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

/**
 * Remember that the login controller was not added because spring security has a default login api call
 * at POST /login
 * with form parameters username, password
 * */

@RestController
public class UserController {

//    private UserRepo repo;
    private final UserRepo repo;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;



    @Autowired
    public UserController(UserRepo repo, UserService userService, PasswordEncoder passwordEncoder) {
        this.repo = repo;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    private User currentUser(Authentication authentication) {
        return repo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + authentication.getName()));
    }

    private void checkSelfOrAdmin(User current, Integer targetId) {
        boolean isAdmin = current.getType() == User.UserCategory.ADMIN;
        boolean isSelf = current.getId().equals(targetId);
        if (!isAdmin && !isSelf) {
            throw new AccessDeniedException("You are not allowed to modify this user");
        }
    }


    @GetMapping("/users")
    public List<User> retrieveAllClients(){
        return repo.findAll();
    }

    // the currently logged in user - used by the frontend to know who's signed in
    @GetMapping("/me")
    public ResponseEntity<User> me(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).build();
        }

        User user = repo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + authentication.getName()));

        return ResponseEntity.ok(user);
    }

    // self-registration - always creates a plain USER (see /users below for admin-created accounts)
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody User user) {

        User savedUser = userService.register(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .build();
    }

    // admin-only: create a user with a chosen role (USER or ADMIN) - enforced by
    // SecurityConfig's exact-path "/users" -> hasRole("ADMIN") rule
    @PostMapping("/users")
    public ResponseEntity<Void> createUser(@RequestBody User user) {

        User savedUser = userService.createByAdmin(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedUser.getId())
                .toUri();

        return ResponseEntity
                .created(location)
                .build();
    }


    @GetMapping("/users/{id}")
    public ResponseEntity<User> retrieveUser(@PathVariable Integer id){
        Optional<User> User = repo.findById(id);

        // remember to add exception
//        if(User.isEmpty()){
//            throw new UserNotFoundException("id:" + id);
//
//       }
        return ResponseEntity.of(User);
    }

    // admin-only - deleting a user also deletes their todos (cascade on User.todos)
    @DeleteMapping("/users/{id}")
    public void deleteClient(@PathVariable Integer id, Authentication authentication){
        User current = currentUser(authentication);
        if (current.getType() != User.UserCategory.ADMIN) {
            throw new AccessDeniedException("Only an admin can delete a user");
        }

        repo.deleteById(id);
    }

    // a user may update their own info; an admin may update anyone's.
    // only merges name/email (and, for an admin, role/status) so this can never
    // wipe out the target's password or silently self-promote to ADMIN.
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateClient(@PathVariable Integer id, @RequestBody User user, Authentication authentication) {
        User current = currentUser(authentication);
        checkSelfOrAdmin(current, id);

        User existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        existing.setFullName(user.getFullName());
        existing.setEmail(user.getEmail());

        if (current.getType() == User.UserCategory.ADMIN) {
            if (user.getType() != null) {
                existing.setType(user.getType());
            }
            existing.setStatus(user.isStatus());

            // admin-only password reset - only touched when a new password is actually sent
            if (user.getPassword() != null && !user.getPassword().isBlank()) {
                existing.setPassword(passwordEncoder.encode(user.getPassword()));
            }
        }

        User updatedUser = repo.save(existing);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(updatedUser.getId())
                .toUri();

        return ResponseEntity.ok().location(location).body(updatedUser);
    }
}
