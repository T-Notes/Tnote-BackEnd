package com.example.tnote.boundedContext.classLog.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClassLogRepository extends JpaRepository<ClassLog, Long> {
    @Query("select cl from ClassLog cl where cl.user.id = :userId")
    List<ClassLog> findAllByUserId(Long userId);

    @Query("select cl from ClassLog cl " +
            "where cl.id = :classLogId and cl.user.id = :userId")
    Optional<ClassLog> findByIdAndUserId(Long userId, Long classLogId);

}
