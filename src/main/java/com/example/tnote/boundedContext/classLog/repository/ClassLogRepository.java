package com.example.tnote.boundedContext.classLog.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClassLogRepository extends JpaRepository<ClassLog, Long> {
    @Query("select cl from ClassLog cl where cl.user.id = :userId and cl.schedule.id = :scheduleId")
    List<ClassLog> findAllByUserIdAndScheduleId(Long userId, Long scheduleId);

    @Query("select c from ClassLog c " +
            "where c.id = :classLogId and c.user.id = :userId")
    Optional<ClassLog> findByIdAndUserId(Long classLogId, Long userId);

    @Query("SELECT c FROM ClassLog c "
            + "WHERE c.user.id = :userId AND c.schedule.id = :scheduleId "
            + "AND c.createdAt >= :startOfDay AND c.createdAt <= :endOfDay")
    List<ClassLog> findByUserIdAndScheduleIdAndStartDateBetween(
            Long userId,
            Long scheduleId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    @Query("SELECT c FROM ClassLog c WHERE c.schedule.id = :scheduleId ORDER BY c.createdAt DESC")
    Slice<ClassLog> findAllByScheduleId(Long scheduleId, Pageable pageable);

    @Query("SELECT c FROM ClassLog c "
            + "WHERE c.user.id = :userId AND c.schedule.id = :scheduleId "
            + "AND c.createdAt >= :startOfDay AND c.createdAt <= :endOfDay ORDER BY c.createdAt DESC")
    Slice<ClassLog> findAllByUserIdAndScheduleIdAndCreatedAtBetween(
            Long userId,
            Long scheduleId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            Pageable pageable);
}
