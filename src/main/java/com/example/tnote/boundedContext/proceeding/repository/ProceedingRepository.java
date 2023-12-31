package com.example.tnote.boundedContext.proceeding.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProceedingRepository extends JpaRepository<Proceeding, Long> {
    List<Proceeding> findAllByUserId(Long userId);
    @Query("select p from Proceeding p " +
            "where p.id = :proceedingId and p.user.id = :userId")
    Optional<Proceeding> findByIdAndUserId(Long proceedingId, Long userId);
}
