package com.pireaus.todoWebApp.controllers;

import com.pireaus.todoWebApp.entities.Todo;
import com.pireaus.todoWebApp.entities.User;
import com.pireaus.todoWebApp.repo.TodoRepo;
import com.pireaus.todoWebApp.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

// don't get confused, todo and task are the same thing!!

@RestController
public class TodoController {

    private TodoRepo todoRepo;
    private UserRepo userRepo;

    @Autowired
    public void setRepository(TodoRepo todoRepo, UserRepo userRepo) {
        this.todoRepo = todoRepo;
        this.userRepo = userRepo;
    }

    private User currentUser(Authentication authentication) {
        return userRepo.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + authentication.getName()));
    }

    private void checkOwnerOrAdmin(User current, Integer ownerId) {
        boolean isAdmin = current.getType() == User.UserCategory.ADMIN;
        boolean isOwner = current.getId().equals(ownerId);
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("You are not allowed to access this todo");
        }
    }

    // all tasks for all users - admin only (enforced in SecurityConfig too)
    @GetMapping("/clients/tasks")
    public List<Todo> retrieveAllClients() {
        return todoRepo.findAll();
    }

    // todos belonging to one user - that user, or an admin
    @GetMapping("/users/{userId}/tasks")
    public List<Todo> getTodosByUser(@PathVariable Integer userId, Authentication authentication) {
        User current = currentUser(authentication);
        checkOwnerOrAdmin(current, userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        return user.getTodos();
    }

    // a single todo - its owner, or an admin
    @GetMapping("/users/tasks/{todoId}")
    public ResponseEntity<Todo> getTodo(@PathVariable Integer todoId, Authentication authentication) {
        User current = currentUser(authentication);
        Todo todo = todoRepo.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + todoId));
        checkOwnerOrAdmin(current, todo.getUser().getId());

        return ResponseEntity.ok(todo);
    }

    @PostMapping("/users/{userId}/tasks")
    public ResponseEntity<Todo> createContact(@PathVariable Integer userId, @RequestBody Todo todo, Authentication authentication) {
        User current = currentUser(authentication);
        checkOwnerOrAdmin(current, userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        todo.setUser(user);
        todo.setCreationDT(LocalDateTime.now());
        todo.setCompletionDT(null);
        todo.setStatus(false);

        Todo savedTodo = todoRepo.save(todo);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{contactId}")
                .buildAndExpand(savedTodo.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedTodo);
    }

    @DeleteMapping("/users/tasks/{todoId}")
    public void deleteContact(@PathVariable Integer todoId, Authentication authentication) {
        User current = currentUser(authentication);
        Todo todo = todoRepo.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + todoId));
        checkOwnerOrAdmin(current, todo.getUser().getId());

        todoRepo.deleteById(todoId);
    }

    // only an admin may alter another user's todo; everyone else may only alter their own
    @PutMapping("/users/tasks/{todoId}")
    public ResponseEntity<Todo> updateContact(@PathVariable Integer todoId, @RequestBody Todo todo, Authentication authentication) {
        User current = currentUser(authentication);
        Todo existing = todoRepo.findById(todoId)
                .orElseThrow(() -> new RuntimeException("Todo not found with id: " + todoId));
        checkOwnerOrAdmin(current, existing.getUser().getId());

        existing.setDescription(todo.getDescription());

        boolean wasCompleted = existing.isStatus();
        boolean nowCompleted = todo.isStatus();
        existing.setStatus(nowCompleted);

        if (!wasCompleted && nowCompleted) {
            existing.setCompletionDT(LocalDateTime.now());
        } else if (!nowCompleted) {
            existing.setCompletionDT(null);
        }

        Todo updatedTodo = todoRepo.save(existing);

        return ResponseEntity.ok(updatedTodo);
    }

}
