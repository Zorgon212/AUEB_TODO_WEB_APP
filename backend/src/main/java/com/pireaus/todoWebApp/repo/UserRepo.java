package com.pireaus.todoWebApp.repo;

import com.pireaus.todoWebApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

}
