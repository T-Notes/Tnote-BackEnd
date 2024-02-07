package com.example.tnote.boundedContext.consultation.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    @Query("select c from Consultation c where c.user.id = :userId and c.schedule.id = :scheduleId")
    List<Consultation> findAllByUserIdAndScheduleId(Long userId, Long scheduleId);
    @Query("SELECT c FROM Consultation c "
            + "WHERE c.user.id = :userId AND c.schedule.id = :scheduleId "
            + "AND c.createdAt >= :startOfDay AND c.createdAt <= :endOfDay")
    List<Consultation> findByUserIdAndScheduleIdAndStartDateBetween(
            Long userId,
            Long scheduleId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    @Query("select c from Consultation c " +
            "where c.id = :consultationId and c.user.id = :userId")
    Optional<Consultation> findByIdAndUserId(Long consultationId, Long userId);


    @Query("SELECT c FROM Consultation c WHERE c.schedule.id = :scheduleId ORDER BY c.createdAt DESC")
    Slice<Consultation> findAllByScheduleId(Long scheduleId, Pageable pageable);

    @Query("SELECT c FROM Consultation c "
            + "WHERE c.user.id = :userId AND c.schedule.id = :scheduleId "
            + "AND c.createdAt >= :startOfDay AND c.createdAt <= :endOfDay ORDER BY c.createdAt DESC")
    Slice<Consultation> findAllByUserIdAndScheduleIdAndCreatedAtBetween(
            Long userId,
            Long scheduleId,
            LocalDateTime startOfDay,
            LocalDateTime endOfDay,
            Pageable pageable);
}
