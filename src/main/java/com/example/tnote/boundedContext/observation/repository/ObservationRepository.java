package com.example.tnote.boundedContext.observation.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.observation.entity.Observation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ObservationRepository extends JpaRepository<Observation, Long> {
    @Query("select o from Observation o where o.user.id = :userId")
    List<Observation> findAllByUserId(Long userId);

    @Query("select o from Observation o " +
            "where o.id = :observationId and o.user.id = :userId")
    Optional<Observation> findByIdAndUserId(Long observationId, Long userId);

    List<Observation> findByUserIdAndStartDateBetween(Long userId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    @Query("SELECT o FROM Observation o ORDER BY o.createdAt DESC")
    Slice<Observation> findAllBy(Pageable pageable);
}
