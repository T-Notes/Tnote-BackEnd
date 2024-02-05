package com.example.tnote.boundedContext.todo.dto;

import com.example.tnote.boundedContext.schedule.entity.Schedule;
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
    private Boolean status;

    public Todo toEntity(User user, Schedule schedule) {
        return Todo.builder()
                .date(this.date)
                .content(this.content)
                .user(user)
                .schedule(schedule)
                .status(false)
                .build();
    }
}
