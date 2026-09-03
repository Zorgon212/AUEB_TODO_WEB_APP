package com.pireaus.todoWebApp.todo.dto;

import com.pireaus.todoWebApp.todo.Todo;

import java.time.LocalDateTime;

// carries only the owning user's id, not the whole nested User - a todo
// doesn't need to leak its owner's email/role/status to whoever can see it
public record TodoResponse(
        Integer id,
        String description,
        boolean status,
        LocalDateTime creationDT,
        LocalDateTime completionDT,
        Integer userId
) {
    public static TodoResponse from(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getDescription(),
                todo.isStatus(),
                todo.getCreationDT(),
                todo.getCompletionDT(),
                todo.getUser() != null ? todo.getUser().getId() : null
        );
    }
}
