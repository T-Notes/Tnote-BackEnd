package com.example.tnote.boundedContext.proceeding.repository;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProceedingRepository extends JpaRepository<Proceeding, Long> {
    List<Proceeding> findAllByUserId(Long userId);
}
