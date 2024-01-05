package com.example.tnote.boundedContext.classLog.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ClassLogRepository extends JpaRepository<ClassLog, Long> {
    @Query("select cl from ClassLog cl where cl.user.id = :userId")
    List<ClassLog> findAllByUserId(Long userId);

    Optional<ClassLog> findByIdAndUserId

    List<ClassLog> findAllByUserId(Long userId);
}
