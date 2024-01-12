package com.example.tnote.boundedContext.todo.repository;

import static com.example.tnote.boundedContext.todo.entity.QTodo.todo;

import com.example.tnote.boundedContext.todo.entity.Todo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class TodoQueryRepository {
    private final JPAQueryFactory query;

    public List<Todo> findAllByUserIdAndScheduleIdAndDate(Long userId, Long scheduleId, LocalDate date) {
        return query
                .selectFrom(todo)
                .where(todo.schedule.user.id.eq(userId)
                        .and(todo.schedule.id.eq(scheduleId))
                        .and(todo.date.eq(date))
                )
                .orderBy(todo.id.asc())
                .fetch();
    }

}