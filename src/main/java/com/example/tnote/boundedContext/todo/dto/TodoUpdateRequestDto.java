package com.example.tnote.boundedContext.todo.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoUpdateRequestDto {

    private LocalDate date;
    private String content;

    public boolean hasDate() {
        return date != null;
    }

    public boolean hasContent() {
        return content != null;
    }
}