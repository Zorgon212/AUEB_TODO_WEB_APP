package com.pireaus.todoWebApp.controllers;

import com.pireaus.todoWebApp.entities.Todo;
import com.pireaus.todoWebApp.entities.User;
import com.pireaus.todoWebApp.repo.TodoRepo;
import com.pireaus.todoWebApp.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

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

    // all tasks for all users
    @GetMapping("/clients/tasks")
    public List<Todo> retrieveAllClients(){
        return todoRepo.findAll();
    }

    @GetMapping("/{userId}/tasks")
    public List<Todo> getTodosByUser(@PathVariable Integer userId) {

        Optional<User> user = userRepo.findById(userId);
//        if (user.isEmpty()) {
//            throw new UserNotFoundException("id:" + userId);
//        }
        return user.get().getTodos();
    }

    @PostMapping("/users/{userId}/tasks")
    public ResponseEntity<Todo> createContact(@PathVariable Integer userId, @RequestBody Todo todo) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        todo.setUser(user);
        Todo savedTodo = todoRepo.save(todo);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{contactId}")
                .buildAndExpand(savedTodo.getId())
                .toUri();

        return ResponseEntity.created(location).body(savedTodo);
    }

    @DeleteMapping("/users/tasks/{taskId}")
    public void deleteContact(@PathVariable Integer todoId){
        todoRepo.deleteById(todoId);
    }

    @PutMapping("/users/tasks/{taskId}")
    public ResponseEntity<Todo> updateContact(@PathVariable Integer todoId, @RequestBody Todo todo) {
        todo.setId(todoId);
        Todo updatedTodo = todoRepo.save(todo);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{taskId}")
                .buildAndExpand(updatedTodo.getId())
                .toUri();

        return ResponseEntity.ok().location(location).body(updatedTodo);
    }

}
