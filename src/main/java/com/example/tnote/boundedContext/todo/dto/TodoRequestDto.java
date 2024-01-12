package com.example.tnote.boundedContext.todo.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoRequestDto {
    private LocalDate date;
    private String content;
}
