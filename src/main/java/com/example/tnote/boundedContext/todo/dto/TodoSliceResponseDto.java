package com.example.tnote.boundedContext.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoSliceResponseDto {
    private List<TodoResponseDto> todos;
    private int numberOfTodo;

    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;
}
