package com.example.tnote.boundedContext.todo.dto;

import com.example.tnote.boundedContext.todo.entity.Todo;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoDeleteResponseDto {
    private Long id;

    public static TodoDeleteResponseDto from(Todo todo) {
        return TodoDeleteResponseDto.builder()
                .id(todo.getId())
                .build();
    }
}
