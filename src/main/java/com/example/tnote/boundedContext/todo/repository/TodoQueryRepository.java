package com.example.tnote.boundedContext.todo.repository;

import static com.example.tnote.boundedContext.todo.entity.QTodo.todo;

import com.example.tnote.boundedContext.todo.entity.Todo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class TodoQueryRepository {
    private final JPAQueryFactory query;

    public List<Todo> findAllByUserIdAndDate(Long userId, Long scheduleId, LocalDate date) {
        return query
                .selectFrom(todo)
                .where(todo.user.id.eq(userId)
                        .and(todo.date.eq(date))
                        .and(todo.schedule.id.eq(scheduleId))
                )
                .orderBy(todo.id.asc())
                .fetch();
    }

    public List<Todo> findByUserIdAndScheduleIdAndDate(Long userId, Long scheduleId, LocalDateTime date) {
        return query
                .selectFrom(todo)
                .where(todo.user.id.eq(userId)
                        .and(todo.schedule.id.eq(scheduleId))
                        .and(todo.createdAt.eq(date))
                )
                .orderBy(todo.createdAt.desc())
                .fetch();
    }

    public List<Todo> findByUserIdAndScheduleIdAndStartDateBetween(Long userId, Long scheduleId,
                                                                   LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return query
                .selectFrom(todo)
                .where(todo.user.id.eq(userId)
                        .and(todo.createdAt.between(startOfDay, endOfDay))
                        .and(todo.schedule.id.eq(scheduleId))
                )
                .orderBy(todo.createdAt.desc())
                .fetch();
    }

    // 추가
    public List<Todo> findByUserIdAndScheduleIdAndYearMonth(Long userId, Long scheduleId, LocalDate date) {
        return query
                .selectFrom(todo)
                .where(todo.user.id.eq(userId)
                        .and(todo.createdAt.year().eq(date.getYear()))
                        .and(todo.createdAt.month().eq(date.getMonthValue()))
                        .and(todo.schedule.id.eq(scheduleId))
                )
                .orderBy(todo.id.asc())
                .fetch();
    }


    public Slice<Todo> findAllByUserIdAndScheduleIdAndCreatedAtBetween(Long userId, Long scheduleId,
                                                                       LocalDateTime startOfDay,
                                                                       LocalDateTime endOfDay, Pageable pageable) {
        List<Todo> todos = query
                .selectFrom(todo)
                .where(todo.user.id.eq(userId)
                        .and(todo.createdAt.between(startOfDay, endOfDay))
                        .and(todo.schedule.id.eq(scheduleId))
                )
                .orderBy(todo.createdAt.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        return LastPage(pageable, todos);
    }

    private Slice<Todo> LastPage(Pageable pageable, List<Todo> todo) {
        boolean hasNext = false;

        if (todo.size() > pageable.getPageSize()) {
            hasNext = true;
            todo.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(todo, pageable, hasNext);
    }
}