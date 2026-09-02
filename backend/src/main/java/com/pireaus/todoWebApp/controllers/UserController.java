package com.pireaus.todoWebApp.controllers;

import com.pireaus.todoWebApp.entities.User;
import com.pireaus.todoWebApp.repo.UserRepo;
import com.pireaus.todoWebApp.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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



    @Autowired
    public UserController(UserRepo repo, UserService userService) {
        this.repo = repo;
        this.userService = userService;
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

//  user creation without security
//    @PostMapping("/users")
//    public ResponseEntity<User> createClient(@RequestBody User user){
//        repo.save(user);
//        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
//                .path("/{id}").buildAndExpand(user.getId()).toUri();
//
//        return ResponseEntity.created(location).build();
//    }
    // with security
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

    @DeleteMapping("/users/{id}")
    public void deleteClient(@PathVariable Integer id){
        repo.deleteById(id);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateClient(@PathVariable Integer id, @RequestBody User user) {
        user.setId(id);
        User updatedUser = repo.save(user);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(updatedUser.getId())
                .toUri();

        return ResponseEntity.ok().location(location).body(updatedUser);
    }
}
