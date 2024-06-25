package com.example.tnote.boundedContext.consultation.repository;

import com.example.tnote.boundedContext.consultation.entity.ConsultationImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ConsultationImageRepository extends JpaRepository<ConsultationImage, Long> {
    List<ConsultationImage> findConsultationImageByConsultationId(Long consultationId);
    @Modifying
    @Query("DELETE FROM ConsultationImage coi WHERE coi.consultation.id = :consultationId")
    void deleteByConsultationId(Long consultationId);
}
