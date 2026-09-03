package com.pireaus.todoWebApp.todo;

import com.pireaus.todoWebApp.todo.dto.CreateTodoRequest;
import com.pireaus.todoWebApp.todo.dto.UpdateTodoRequest;
import com.pireaus.todoWebApp.user.User;
import com.pireaus.todoWebApp.user.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

// TodoService with its repositories mocked out - covers ownership
// enforcement and the create/complete flows, without needing a real database.
@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepo todoRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private TodoService todoService;

    @Test
    void createAssignsTheNewTodoToItsOwner() {
        User owner = existingUser(1, "owner@example.com", User.UserCategory.USER);
        when(userRepo.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(userRepo.findById(1)).thenReturn(Optional.of(owner));
        when(todoRepo.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = todoService.create(1, new CreateTodoRequest("Buy milk"), "owner@example.com");

        assertThat(response.description()).isEqualTo("Buy milk");
        assertThat(response.userId()).isEqualTo(1);
        assertThat(response.status()).isFalse();
    }

    @Test
    void aUserCannotSeeAnotherUsersTodos() {
        User outsider = existingUser(2, "outsider@example.com", User.UserCategory.USER);
        when(userRepo.findByEmail("outsider@example.com")).thenReturn(Optional.of(outsider));

        assertThatThrownBy(() -> todoService.findAllForUser(1, "outsider@example.com"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void updatingWithStatusTrueStampsTheCompletionTime() {
        User owner = existingUser(1, "owner@example.com", User.UserCategory.USER);
        Todo existing = new Todo();
        existing.setId(10);
        existing.assignTo(owner);

        when(userRepo.findByEmail("owner@example.com")).thenReturn(Optional.of(owner));
        when(todoRepo.findById(10)).thenReturn(Optional.of(existing));
        when(todoRepo.save(any(Todo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = todoService.update(10, new UpdateTodoRequest("Buy milk", true), "owner@example.com");

        assertThat(response.status()).isTrue();
        assertThat(response.completionDT()).isNotNull();
    }

    private User existingUser(int id, String email, User.UserCategory type) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.promoteTo(type);
        return user;
    }
}
