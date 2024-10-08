package com.example.tnote.boundedContext.observation.repository;

import com.example.tnote.boundedContext.observation.entity.Observation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ObservationRepository extends JpaRepository<Observation, Long> {
    @Query("select o from Observation o where o.user.id = :userId and o.schedule.id = :scheduleId")
    List<Observation> findAllByUserIdAndScheduleId(Long userId, Long scheduleId);

    @Query("select o from Observation o " +
            "where o.id = :observationId and o.user.id = :userId")
    Optional<Observation> findByIdAndUserId(Long observationId, Long userId);

    @Query("SELECT o FROM Observation o "
            + "WHERE o.user.id = :userId AND o.schedule.id = :scheduleId "
            + "AND o.startDate <= :endOfDay AND o.endDate >= :startOfDay")
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

    @Query("SELECT o FROM Observation o " +
            "WHERE o.user.id = :userId " +
            "AND o.schedule.id = :scheduleId " +
            "AND ((" +
            "FUNCTION('YEAR', o.startDate) = FUNCTION('YEAR', :date) AND FUNCTION('MONTH', o.startDate) = FUNCTION('MONTH', :date)"
            +
            ") OR (" +
            "FUNCTION('YEAR', o.endDate) = FUNCTION('YEAR', :date) AND FUNCTION('MONTH', o.endDate) = FUNCTION('MONTH', :date)"
            +
            "))")
    List<Observation> findByUserIdAndScheduleIdAndYearMonth(
            Long userId,
            Long scheduleId,
            LocalDate date);

    @Query("SELECT o FROM Observation o WHERE o.user.id = :userId AND o.title LIKE %:keyword% AND o.startDate >= :startDate AND o.endDate <= :endDate")
    List<Observation> findByTitleContaining(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId);

    @Query("SELECT o FROM Observation o WHERE o.user.id = :userId AND (o.title LIKE %:keyword% OR o.observationContents LIKE %:keyword%) AND o.startDate >= :startDate AND o.endDate <= :endDate")
    List<Observation> findByTitleOrPlanOrClassContentsContaining(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId);

    @Query("SELECT o FROM Observation o WHERE o.user.id = :userId AND o.observationContents LIKE %:keyword% AND o.startDate >= :startDate AND o.endDate <= :endDate")
    List<Observation> findByContentsContaining(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId);

    void deleteAllByUserId(Long userId);
}
