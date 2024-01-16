package com.example.tnote.boundedContext.consultation.repository;

import com.example.tnote.boundedContext.consultation.entity.ConsultationImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationImageRepository extends JpaRepository<ConsultationImage, Long> {
    List<ConsultationImage> findConsultationImageByConsultation_Id(Long consultationId);
}
