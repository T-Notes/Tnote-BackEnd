package com.example.tnote.boundedContext.proceeding.repository;

import com.example.tnote.boundedContext.proceeding.entity.ProceedingImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProceedingImageRepository extends JpaRepository<ProceedingImage, Long> {
    List<ProceedingImage> findProceedingImageById(Long proceedingId);
    void deleteByProceedingId(Long proceedingId);
}
