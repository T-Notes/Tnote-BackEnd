package com.example.tnote.boundedContext.classLog.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.exception.ClassLogErrorCode;
import com.example.tnote.boundedContext.classLog.exception.ClassLogException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClassLogRepository extends JpaRepository<ClassLog, Long> {

    default ClassLog findClassLogById(Long id) {
        return findById(id)
                .orElseThrow(() -> new ClassLogException(ClassLogErrorCode.CLASS_LOG_NOT_FOUNT));
    }

    @Query("select cl from ClassLog cl where cl.user.id = :userId and cl.schedule.id = :scheduleId")
    List<ClassLog> findAllByUserIdAndScheduleId(Long userId, Long scheduleId);

    @Query("select c from ClassLog c " +
            "where c.id = :classLogId and c.user.id = :userId")
    Optional<ClassLog> findByIdAndUserId(Long classLogId, Long userId);

    @Query("SELECT c FROM ClassLog c "
            + "WHERE c.user.id = :userId AND c.schedule.id = :scheduleId "
            + "AND c.startDate <= :endOfDay AND c.endDate >= :startOfDay")
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

    @Query("SELECT c FROM ClassLog c " +
            "WHERE c.user.id = :userId " +
            "AND c.schedule.id = :scheduleId " +
            "AND ((" +
            "FUNCTION('YEAR', c.startDate) = FUNCTION('YEAR', :date) AND FUNCTION('MONTH', c.startDate) = FUNCTION('MONTH', :date)"
            +
            ") OR (" +
            "FUNCTION('YEAR', c.endDate) = FUNCTION('YEAR', :date) AND FUNCTION('MONTH', c.endDate) = FUNCTION('MONTH', :date)"
            +
            "))")
    List<ClassLog> findByUserIdAndScheduleIdAndYearMonth(
            Long userId,
            Long scheduleId,
            LocalDate date);

    @Query("SELECT c FROM ClassLog c WHERE c.user.id = :userId AND c.title LIKE %:keyword% AND c.startDate >= :startDate AND c.endDate <= :endDate")
    List<ClassLog> findByTitleContaining(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId);

    @Query("SELECT c FROM ClassLog c WHERE c.user.id = :userId AND (c.title LIKE %:keyword% OR c.plan LIKE %:keyword% OR c.classContents LIKE %:keyword%) AND c.startDate >= :startDate AND c.endDate <= :endDate")
    List<ClassLog> findByTitleOrPlanOrClassContentsContaining(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId);


    @Query("SELECT c FROM ClassLog c WHERE c.user.id = :userId AND c.classContents LIKE %:keyword% AND c.startDate >= :startDate AND c.endDate <= :endDate")
    List<ClassLog> findByContentsContaining(
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("userId") Long userId);

    void deleteAllByUserId(Long userId);
}
