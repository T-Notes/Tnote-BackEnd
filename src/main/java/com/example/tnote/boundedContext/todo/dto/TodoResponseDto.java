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
    private Boolean status;

    public static TodoResponseDto from(Todo todo) {

        return TodoResponseDto.builder()
                .id(todo.getId())
                .content(todo.getContent())
                .date(todo.getDate())
                .status(todo.getStatus())
                .build();
    }

    public static List<TodoResponseDto> from(List<Todo> todo) {
        return todo.stream()
                .map(TodoResponseDto::from)
                .toList();
    }
}
