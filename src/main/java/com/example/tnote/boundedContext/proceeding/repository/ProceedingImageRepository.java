package com.example.tnote.boundedContext.proceeding.repository;

import com.example.tnote.boundedContext.proceeding.entity.ProceedingImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ProceedingImageRepository extends JpaRepository<ProceedingImage, Long> {
    List<ProceedingImage> findProceedingImageByProceedingId(Long proceedingId);
    @Modifying
    @Query("DELETE FROM ProceedingImage pi WHERE pi.proceeding.id = :proceedingId")
    void deleteByProceedingId(Long proceedingId);
}
