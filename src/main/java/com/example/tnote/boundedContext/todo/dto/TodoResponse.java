package com.example.tnote.boundedContext.todo.dto;

import com.example.tnote.boundedContext.todo.entity.Todo;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TodoResponse {

    private Long id;
    private String content;
    private LocalDate date;
    private Boolean status;

    public static TodoResponse from(Todo todo) {

        return TodoResponse.builder()
                .id(todo.getId())
                .content(todo.getContent())
                .date(todo.getDate())
                .status(todo.getStatus())
                .build();
    }

    public static List<TodoResponse> from(List<Todo> todo) {
        return todo.stream()
                .map(TodoResponse::from)
                .toList();
    }
}
