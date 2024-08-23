package com.example.tnote.boundedContext.todo.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoUpdateRequest {

    private LocalDate date;
    private String content;
    private Boolean status;

    public boolean hasDate() {
        return date != null;
    }

    public boolean hasContent() {
        return content != null;
    }

    public boolean hasStatus() {
        return status != null;
    }
}