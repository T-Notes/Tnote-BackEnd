package com.example.tnote.boundedContext.todo.dto;

import com.example.tnote.boundedContext.todo.entity.Todo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoDeleteResponse {
    private Long id;

    public static TodoDeleteResponse from(Todo todo) {
        return TodoDeleteResponse.builder()
                .id(todo.getId())
                .build();
    }
}
