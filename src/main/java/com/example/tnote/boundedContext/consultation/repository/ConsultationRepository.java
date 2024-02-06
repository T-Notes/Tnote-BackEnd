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

    @Query("select c from Consultation c " +
            "where c.id = :consultationId and c.user.id = :userId")
    Optional<Consultation> findByIdAndUserId(Long consultationId, Long userId);

    List<Consultation> findByUserIdAndStartDateBetween(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT c FROM Consultation c WHERE c.schedule.id = :scheduleId ORDER BY c.createdAt DESC")
    Slice<Consultation> findAllByScheduleId(Long scheduleId, Pageable pageable);
}
