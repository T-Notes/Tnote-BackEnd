package com.example.tnote.boundedContext.consultation.dto;

import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.home.constant.LogType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsultationResponseDto {
    private Long id;
    private String studentName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CounselingField counselingField;
    private CounselingType counselingType;
    private String consultationContents;
    private String consultationResult;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String logType;
    public static ConsultationResponseDto of(Consultation consultation) {
        return ConsultationResponseDto.builder()
                .id(consultation.getId())
                .studentName(consultation.getStudentName())
                .startDate(consultation.getStartDate())
                .endDate(consultation.getEndDate())
                .counselingField(consultation.getCounselingField())
                .counselingType(consultation.getCounselingType())
                .consultationContents(consultation.getConsultationContents())
                .consultationResult(consultation.getConsultationResult())
                .createdAt(consultation.getCreatedAt())
                .updatedAt(consultation.getUpdatedAt())
                .logType(LogType.CONSULTATION.name())
                .build();
    }
}
