package com.example.tnote.boundedContext.todo.repository;

import com.example.tnote.boundedContext.todo.entity.Todo;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    @Query("SELECT t FROM Todo t "
            + "WHERE t.user.id = :userId AND t.schedule.id = :scheduleId "
            + "AND t.createdAt >= :startOfDay AND t.createdAt <= :endOfDay")
    List<Todo> findByUserIdAndScheduleIdAndStartDateBetween(
            Long userId,
            Long scheduleId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    @Query("SELECT t FROM Todo t "
            + "WHERE t.user.id = :userId AND t.schedule.id = :scheduleId "
            + "AND t.createdAt >= :startOfDay AND t.createdAt <= :endOfDay ORDER BY t.createdAt DESC")
    Slice<Todo> findAllByUserIdAndScheduleIdAndCreatedAtBetween(
            Long userId,
            Long scheduleId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            Pageable pageable);
}
