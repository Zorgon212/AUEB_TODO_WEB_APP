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

// todo and task are the same thing
@RestController
@Tag(name = "Todos", description = "todo(task) api calls")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    // security configuration enforces it
    @Operation(summary = "All todos for all users this can only be called from an admin account")
    @GetMapping("/clients/tasks")
    public List<TodoResponse> retrieveAllClients() {
        return todoService.findAll();
    }

    // todos belonging to one user - that user, or an admin
    @Operation(summary = "Todos that belong to a specific user")
    @GetMapping("/users/{userId}/tasks")
    public List<TodoResponse> getTodosByUser(@PathVariable Integer userId, Authentication authentication) {
        return todoService.findAllForUser(userId, authentication.getName());
    }

    // a single todo - its owner, or an admin
    @Operation(summary = "one todo returned based on its id")
    @GetMapping("/users/tasks/{todoId}")
    public ResponseEntity<TodoResponse> getTodo(@PathVariable Integer todoId, Authentication authentication) {
        return ResponseEntity.ok(todoService.findById(todoId, authentication.getName()));
    }

    @Operation(summary = "todo creation call")
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

    @Operation(summary = "todo deletion call")
    @DeleteMapping("/users/tasks/{todoId}")
    public void deleteContact(@PathVariable Integer todoId, Authentication authentication) {
        todoService.delete(todoId, authentication.getName());
    }

    // simple users can only update their own, admin can update tasks belonging to others as well
    @Operation(summary = "todo update call")
    @PutMapping("/users/tasks/{todoId}")
    public ResponseEntity<TodoResponse> updateContact(
            @PathVariable Integer todoId,
            @RequestBody UpdateTodoRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(todoService.update(todoId, request, authentication.getName()));
    }
}
