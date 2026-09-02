package com.pireaus.todoWebApp.services;

import com.pireaus.todoWebApp.entities.User;
import com.pireaus.todoWebApp.exceptions.EmailAlreadyExistsException;
import com.pireaus.todoWebApp.repo.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepo userRepo,
            PasswordEncoder passwordEncoder) {

        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(User user) {

        if (userRepo.findByEmail(user.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(
                    "This email is already registered: " + user.getEmail());
        }

        user.setPassword(
                passwordEncoder.encode(user.getPassword())
        );

        user.setType(User.UserCategory.USER);

        user.setStatus(true);

        return userRepo.save(user);
    }
}
