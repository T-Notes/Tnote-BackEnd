package com.example.tnote.boundedContext.classLog.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClassLogRepository extends JpaRepository<ClassLog, Long> {
    @Query("select cl from ClassLog cl " +
            "where cl.id = :reviewId and cl.user.id = :userId")
    Optional<ClassLog> findByIdAndUserId(Long reviewId, Long userId);
}
