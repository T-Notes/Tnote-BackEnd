package com.example.tnote.boundedContext.observation.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.observation.entity.Observation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ObservationRepository extends JpaRepository<Observation, Long> {
    @Query("select o from Observation o where o.user.id = :userId and o.schedule.id = :scheduleId")
    List<Observation> findAllByUserIdAndScheduleId(Long userId, Long scheduleId);

    @Query("select o from Observation o " +
            "where o.id = :observationId and o.user.id = :userId")
    Optional<Observation> findByIdAndUserId(Long observationId, Long userId);

    @Query("SELECT o FROM Observation o "
            + "WHERE o.user.id = :userId AND o.schedule.id = :scheduleId "
            + "AND o.createdAt >= :startOfDay AND o.createdAt <= :endOfDay")
    List<Observation> findByUserIdAndScheduleIdAndStartDateBetween(
            Long userId,
            Long scheduleId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);
    @Query("SELECT o FROM Observation o where o.schedule.id = :scheduleId ORDER BY o.createdAt DESC")
    Slice<Observation> findAllByScheduleId(Long scheduleId, Pageable pageable);

    @Query("SELECT o FROM Observation o "
            + "WHERE o.user.id = :userId AND o.schedule.id = :scheduleId "
            + "AND o.createdAt >= :startOfDay AND o.createdAt <= :endOfDay ORDER BY o.createdAt DESC")
    Slice<Observation> findAllByUserIdAndScheduleIdAndCreatedAtBetween(
            Long userId,
            Long scheduleId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            Pageable pageable);
}
