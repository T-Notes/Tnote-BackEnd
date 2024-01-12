package com.example.tnote.boundedContext.todo.repository;

import com.example.tnote.boundedContext.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
}
