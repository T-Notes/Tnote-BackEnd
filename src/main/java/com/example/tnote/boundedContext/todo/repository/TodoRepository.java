package com.example.tnote.boundedContext.todo.repository;

import com.example.tnote.boundedContext.todo.entity.Todo;
import com.example.tnote.boundedContext.todo.exception.TodoErrorCode;
import com.example.tnote.boundedContext.todo.exception.TodoException;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {
//    @Query("SELECT t FROM Todo t "
//            + "WHERE t.user.id = :userId AND t.schedule.id = :scheduleId "
//            + "AND t.createdAt >= :startOfDay AND t.createdAt <= :endOfDay")
//    List<Todo> findByUserIdAndScheduleIdAndStartDateBetween(
//            Long userId,
//            Long scheduleId,
//            LocalDateTime startOfDay,
//            LocalDateTime endOfDay);
//
//    @Query("SELECT t FROM Todo t "
//            + "WHERE t.user.id = :userId AND t.schedule.id = :scheduleId "
//            + "AND t.createdAt >= :startOfDay AND t.createdAt <= :endOfDay ORDER BY t.createdAt DESC")
//    Slice<Todo> findAllByUserIdAndScheduleIdAndCreatedAtBetween(
//            Long userId,
//            Long scheduleId,
//            LocalDateTime startOfDay,
//            LocalDateTime endOfDay,
//            Pageable pageable);
//
//    @Query("SELECT t FROM Todo t " +
//            "WHERE t.user.id = :userId " +
//            "AND t.schedule.id = :scheduleId " +
//            "AND FUNCTION('YEAR', t.createdAt) = FUNCTION('YEAR', :date) " +
//            "AND FUNCTION('MONTH', t.createdAt) = FUNCTION('MONTH', :date)")
//    List<Todo> findByUserIdAndScheduleIdAndYearMonth(
//            Long userId,
//            Long scheduleId,
//            LocalDate date);

    default Todo findTodoById(Long id) {
        return findById(id)
                .orElseThrow(() -> new TodoException(TodoErrorCode.TODO_NOT_FOUND));
    }
}
