package com.pireaus.todoWebApp.user;

import com.pireaus.todoWebApp.common.exception.EmailAlreadyExistsException;
import com.pireaus.todoWebApp.user.dto.RegisterUserRequest;
import com.pireaus.todoWebApp.user.dto.UpdateUserRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// UserService with its repository/password-encoder mocked out - covers the
// business rules and authorization guards, without needing a real database.
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void registerSavesAPlainUserWithAnEncodedPassword() {
        var request = new RegisterUserRequest("Ada Lovelace", "ada@example.com", "raw-password");
        when(userRepo.findByEmail("ada@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("raw-password")).thenReturn("encoded-password");
        when(userRepo.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = userService.register(request);

        assertThat(response.email()).isEqualTo("ada@example.com");
        assertThat(response.type()).isEqualTo(User.UserCategory.USER);

        var captor = ArgumentCaptor.forClass(User.class);
        verify(userRepo).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-password");
    }

    @Test
    void registerRejectsAnEmailThatIsAlreadyTaken() {
        var request = new RegisterUserRequest("Ada Lovelace", "ada@example.com", "raw-password");
        when(userRepo.findByEmail("ada@example.com")).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.register(request))
                .isInstanceOf(EmailAlreadyExistsException.class);

        verify(userRepo, never()).save(any());
    }

    @Test
    void aRegularUserCannotUpdateSomeoneElsesProfile() {
        User actingUser = existingUser(1, "member@example.com", User.UserCategory.USER);
        when(userRepo.findByEmail("member@example.com")).thenReturn(Optional.of(actingUser));

        var request = new UpdateUserRequest("New Name", "new@example.com", null, null, null);

        assertThatThrownBy(() -> userService.update(2, request, "member@example.com"))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void onlyAnAdminCanDeleteAUser() {
        User actingUser = existingUser(1, "member@example.com", User.UserCategory.USER);
        when(userRepo.findByEmail("member@example.com")).thenReturn(Optional.of(actingUser));

        assertThatThrownBy(() -> userService.delete(2, "member@example.com"))
                .isInstanceOf(AccessDeniedException.class);

        verify(userRepo, never()).deleteById(any());
    }

    private User existingUser(int id, String email, User.UserCategory type) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.promoteTo(type);
        return user;
    }
}
