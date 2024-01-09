package com.example.tnote.boundedContext.consultation.dto;

import lombok.Getter;

@Getter
public class ConsultationUpdateRequestDto {
    private String consultationContents;
    private String consultationResult;

    public boolean hasConsultationContents() {
        return consultationContents != null;
    }

    public boolean hasConsultationResult() {
        return consultationResult != null;
    }
}
