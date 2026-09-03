package com.pireaus.todoWebApp.todo;

import com.pireaus.todoWebApp.common.exception.NotFoundException;
import com.pireaus.todoWebApp.todo.dto.CreateTodoRequest;
import com.pireaus.todoWebApp.todo.dto.TodoResponse;
import com.pireaus.todoWebApp.todo.dto.UpdateTodoRequest;
import com.pireaus.todoWebApp.user.User;
import com.pireaus.todoWebApp.user.UserRepo;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

// application service: orchestrates the Todo entity + enforces "owner or
// admin" access. Controllers stay thin HTTP adapters that just call in here.
@Service
public class TodoService {

    private final TodoRepo todoRepo;
    private final UserRepo userRepo;

    public TodoService(TodoRepo todoRepo, UserRepo userRepo) {
        this.todoRepo = todoRepo;
        this.userRepo = userRepo;
    }

    // admin only (also enforced at the route level in SecurityConfig)
    public List<TodoResponse> findAll() {
        return todoRepo.findAll().stream().map(TodoResponse::from).toList();
    }

    // todos belonging to one user - that user, or an admin
    public List<TodoResponse> findAllForUser(Integer userId, String actingUserEmail) {
        User current = currentUser(actingUserEmail);
        requireOwnerOrAdmin(current, userId);

        User owner = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        return owner.getTodos().stream().map(TodoResponse::from).toList();
    }

    // a single todo - its owner, or an admin
    public TodoResponse findById(Integer todoId, String actingUserEmail) {
        User current = currentUser(actingUserEmail);
        Todo todo = getOrThrow(todoId);
        requireOwnerOrAdmin(current, todo.getUser().getId());

        return TodoResponse.from(todo);
    }

    public TodoResponse create(Integer userId, CreateTodoRequest request, String actingUserEmail) {
        User current = currentUser(actingUserEmail);
        requireOwnerOrAdmin(current, userId);

        User owner = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + userId));

        Todo todo = new Todo();
        todo.setDescription(request.description());
        todo.assignTo(owner);

        return TodoResponse.from(todoRepo.save(todo));
    }

    // only an admin may alter another user's todo; everyone else may only alter their own
    public TodoResponse update(Integer todoId, UpdateTodoRequest request, String actingUserEmail) {
        User current = currentUser(actingUserEmail);
        Todo existing = getOrThrow(todoId);
        requireOwnerOrAdmin(current, existing.getUser().getId());

        existing.updateDescription(request.description());

        boolean wasCompleted = existing.isStatus();
        boolean nowCompleted = request.status();

        if (!wasCompleted && nowCompleted) {
            existing.complete();
        } else if (!nowCompleted) {
            existing.reopen();
        }

        return TodoResponse.from(todoRepo.save(existing));
    }

    public void delete(Integer todoId, String actingUserEmail) {
        User current = currentUser(actingUserEmail);
        Todo existing = getOrThrow(todoId);
        requireOwnerOrAdmin(current, existing.getUser().getId());

        todoRepo.deleteById(todoId);
    }

    // --- shared lookups / guards --------------------------------------------

    private Todo getOrThrow(Integer todoId) {
        return todoRepo.findById(todoId)
                .orElseThrow(() -> new NotFoundException("Todo not found with id: " + todoId));
    }

    private User currentUser(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found: " + email));
    }

    private void requireOwnerOrAdmin(User current, Integer ownerId) {
        if (!current.isAdmin() && !current.isSameUserAs(ownerId)) {
            throw new AccessDeniedException("You are not allowed to access this todo");
        }
    }
}
