package com.example.tnote.boundedContext.todo.dto;

import com.example.tnote.boundedContext.todo.entity.Todo;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TodoRequestDto {
    private LocalDate date;
    private String content;

    public Todo toEntity(User user) {
        return Todo.builder()
                .date(this.date)
                .content(this.content)
                .user(user)
                .build();
    }
}
