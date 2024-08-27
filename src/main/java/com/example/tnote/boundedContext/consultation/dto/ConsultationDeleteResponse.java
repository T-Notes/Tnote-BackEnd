package com.example.tnote.boundedContext.consultation.dto;

import com.example.tnote.boundedContext.consultation.entity.Consultation;
import lombok.Getter;

@Getter
public class ConsultationDeleteResponse {
    private Long id;

    public ConsultationDeleteResponse() {
    }

    public ConsultationDeleteResponse(Long id) {
        this.id = id;
    }

    public static ConsultationDeleteResponse from(final Consultation consultation) {
        return new ConsultationDeleteResponse(consultation.getId());
    }
}
