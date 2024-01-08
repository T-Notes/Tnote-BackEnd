package com.example.tnote.boundedContext.consultation.repository;

import com.example.tnote.boundedContext.consultation.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
}
