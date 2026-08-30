package com.pireaus.todoWebApp.repo;

import com.pireaus.todoWebApp.entities.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepo extends JpaRepository<Todo, Integer> {
}
