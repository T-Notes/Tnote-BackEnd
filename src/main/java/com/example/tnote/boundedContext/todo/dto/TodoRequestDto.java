package com.example.tnote.boundedContext.todo.dto;

import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.todo.entity.Todo;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TodoRequestDto {
    private String content;
    private Boolean status;

    public Todo toEntity(User user, Schedule schedule, LocalDate date) {
        return Todo.builder()
                .date(date)
                .content(this.content)
                .user(user)
                .schedule(schedule)
                .status(false)
                .build();
    }
}
