package com.example.tnote.boundedContext.proceeding.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProceedingRepository extends JpaRepository<Proceeding, Long> {
    @Query("select p from Proceeding p where p.user.id = :userId and p.schedule.id = :scheduleId")
    List<Proceeding> findAllByUserIdAndScheduleId(Long userId, Long scheduleId);

    @Query("select p from Proceeding p " +
            "where p.id = :proceedingId and p.user.id = :userId")
    Optional<Proceeding> findByIdAndUserId(Long proceedingId, Long userId);

    @Query("SELECT p FROM Proceeding p "
            + "WHERE p.user.id = :userId AND p.schedule.id = :scheduleId "
            + "AND p.startDate >= :startOfDay AND p.endDate <= :endOfDay")
    List<Proceeding> findByUserIdAndScheduleIdAndStartDateBetween(
            Long userId,
            Long scheduleId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    @Query("SELECT p FROM Proceeding p where p.schedule.id = :scheduleId ORDER BY p.createdAt DESC")
    Slice<Proceeding> findAllByScheduleId(Long scheduleId, Pageable pageable);

    @Query("SELECT p FROM Proceeding p "
            + "WHERE p.user.id = :userId AND p.schedule.id = :scheduleId "
            + "AND p.createdAt >= :startOfDay AND p.createdAt <= :endOfDay ORDER BY p.createdAt DESC")
    Slice<Proceeding> findAllByUserIdAndScheduleIdAndCreatedAtBetween(
            Long userId,
            Long scheduleId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            Pageable pageable);

    @Query("SELECT p FROM Proceeding p " +
            "WHERE p.user.id = :userId " +
            "AND p.schedule.id = :scheduleId " +
            "AND FUNCTION('YEAR', p.createdAt) = FUNCTION('YEAR', :date) " +
            "AND FUNCTION('MONTH', p.createdAt) = FUNCTION('MONTH', :date)")
    List<Proceeding> findByUserIdAndScheduleIdAndYearMonth(
            Long userId,
            Long scheduleId,
            LocalDate date);

    void deleteAllByUserId(Long userId);
}
