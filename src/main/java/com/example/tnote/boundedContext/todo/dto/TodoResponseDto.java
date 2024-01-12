package com.example.tnote.boundedContext.todo.dto;

import com.example.tnote.boundedContext.todo.entity.Todo;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TodoResponseDto {

    private Long id;
    private String content;
    private LocalDate date;

    public static TodoResponseDto of(Todo todo) {

        return TodoResponseDto.builder()
                .id(todo.getId())
                .content(todo.getContent())
                .date(todo.getDate())
                .build();
    }

    public static List<TodoResponseDto> of(List<Todo> todo) {
        return todo.stream()
                .map(TodoResponseDto::of)
                .toList();
    }
}
