package com.pireaus.todoWebApp.todo;

import com.pireaus.todoWebApp.todo.dto.CreateTodoRequest;
import com.pireaus.todoWebApp.todo.dto.TodoResponse;
import com.pireaus.todoWebApp.todo.dto.UpdateTodoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

// don't get confused, todo and task are the same thing!!
@RestController
@Tag(name = "Todos", description = "Per-user todo (task) management")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // all tasks for all users - admin only (enforced in SecurityConfig too)
    @Operation(summary = "All todos for all users (admin only)")
    @GetMapping("/clients/tasks")
    public List<TodoResponse> retrieveAllClients() {
        return todoService.findAll();
    }

    // todos belonging to one user - that user, or an admin
    @Operation(summary = "Todos belonging to one user - that user, or an admin")
    @GetMapping("/users/{userId}/tasks")
    public List<TodoResponse> getTodosByUser(@PathVariable Integer userId, Authentication authentication) {
        return todoService.findAllForUser(userId, authentication.getName());
    }

    // a single todo - its owner, or an admin
    @Operation(summary = "A single todo - its owner, or an admin")
    @GetMapping("/users/tasks/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable Integer todoId, Authentication authentication) {
        return ResponseEntity.ok(todoService.findById(todoId, authentication.getName()));
    }

    @Operation(summary = "Create a todo for a user - that user, or an admin")
    @PostMapping("/users/{userId}/tasks")
    public ResponseEntity<TodoResponse> createContact(
            @PathVariable Integer userId,
            @RequestBody CreateTodoRequest request,
            Authentication authentication
    ) {
        TodoResponse saved = todoService.create(userId, request, authentication.getName());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{contactId}")
                .buildAndExpand(saved.id())
                .toUri();

        return ResponseEntity.created(location).body(saved);
    }

    @Operation(summary = "Delete a todo - its owner, or an admin")
    @DeleteMapping("/users/tasks/{todoId}")
    public void deleteContact(@PathVariable Integer todoId, Authentication authentication) {
        todoService.delete(todoId, authentication.getName());
    }

    // only an admin may alter another user's todo; everyone else may only alter their own
    @Operation(summary = "Update a todo's description/completion - its owner, or an admin")
    @PutMapping("/users/tasks/{todoId}")
    public ResponseEntity<TodoResponse> updateContact(
            @PathVariable Integer todoId,
            @RequestBody UpdateTodoRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(todoService.update(todoId, request, authentication.getName()));
    }
}
