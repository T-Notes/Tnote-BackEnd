package com.example.tnote.boundedContext.consultation.dto;

import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ConsultationUpdateRequestDto {
    private String studentName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CounselingField counselingField;
    private CounselingType counselingType;
    private String consultationContents;
    private String consultationResult;

    public boolean hasStudentName() {
        return studentName != null;
    }

    public boolean hasStartDate() {
        return startDate != null;
    }

    public boolean hasEndDate() {
        return endDate != null;
    }

    public boolean hasCounselingField() {
        return counselingField != null;
    }

    public boolean hasCounselingType() {
        return counselingType != null;
    }

    public boolean hasConsultationContents() {
        return consultationContents != null;
    }

    public boolean hasConsultationResult() {
        return consultationResult != null;
    }
}
