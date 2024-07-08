package com.example.tnote.boundedContext.consultation.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    @Query("select c from Consultation c where c.user.id = :userId and c.schedule.id = :scheduleId")
    List<Consultation> findAllByUserIdAndScheduleId(Long userId, Long scheduleId);
    @Query("SELECT c FROM Consultation c "
            + "WHERE c.user.id = :userId AND c.schedule.id = :scheduleId "
            + "AND c.startDate <= :endOfDay AND c.endDate >= :startOfDay")
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

    @Query("SELECT c FROM Consultation c " +
            "WHERE c.user.id = :userId " +
            "AND c.schedule.id = :scheduleId " +
            "AND ((" +
            "FUNCTION('YEAR', c.startDate) = FUNCTION('YEAR', :date) AND FUNCTION('MONTH', c.startDate) = FUNCTION('MONTH', :date)" +
            ") OR (" +
            "FUNCTION('YEAR', c.endDate) = FUNCTION('YEAR', :date) AND FUNCTION('MONTH', c.endDate) = FUNCTION('MONTH', :date)" +
            "))")
    List<Consultation> findByUserIdAndScheduleIdAndYearMonth(
            Long userId,
            Long scheduleId,
            LocalDate date);

    @Query("SELECT c FROM Consultation c WHERE c.user.id = :userId AND c.title LIKE %:keyword% AND c.startDate >= :startDate AND c.endDate <= :endDate")
    List<Consultation> findByTitleContaining(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId);

    @Query("SELECT c FROM Consultation c WHERE c.user.id = :userId AND (c.title LIKE %:keyword% OR c.consultationContents LIKE %:keyword%) AND c.startDate >= :startDate AND c.endDate <= :endDate")
    List<Consultation> findByTitleOrPlanOrClassContentsContaining(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId);

    void deleteAllByUserId(Long userId);
}
