package com.example.tnote.boundedContext.consultation.dto;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.EnumSet;
import lombok.Getter;

@Getter
public class ConsultationUpdateRequestDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CounselingField counselingField;
    private CounselingType counselingType;
    private String consultationContents;
    private String consultationResult;

    public boolean hasStudentName() {
        return title != null;
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
    public void validateEnums() {
        if (hasCounselingField() && !EnumSet.allOf(CounselingField.class).contains(counselingField)) {
            throw CustomException.INVALID_COUNSELING_FIELD;
        }

        if (hasCounselingType() && !EnumSet.allOf(CounselingType.class).contains(counselingType)) {
            throw CustomException.INVALID_COUNSELING_TYPE;
        }
    }
}
