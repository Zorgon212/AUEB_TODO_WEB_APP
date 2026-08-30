package com.pireaus.todoWebApp.repo;

import com.pireaus.todoWebApp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer> {
}
