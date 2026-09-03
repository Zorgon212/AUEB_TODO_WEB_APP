package com.pireaus.todoWebApp.todo.dto;

// creationDT/completionDT/status are always set server-side (see Todo.assignTo) -
// the client only ever supplies what the todo is
public record CreateTodoRequest(String description) {
}
