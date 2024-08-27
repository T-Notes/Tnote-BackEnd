package com.example.tnote.boundedContext.consultation.dto;

import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.consultation.exception.ConsultationErrorCode;
import com.example.tnote.boundedContext.consultation.exception.ConsultationException;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.Getter;

@Getter
public class ConsultationSaveRequest {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CounselingField counselingField;
    private CounselingType counselingType;
    private String consultationContents;
    private String consultationResult;
    private boolean isAllDay;
    private String color;

    public Consultation toEntity(User user, Schedule schedule) {
        validateEnums();
        return new Consultation(this.title, this.startDate, this.endDate, this.counselingField, this.counselingType,
                this.consultationContents, this.consultationResult, this.color, new ArrayList<>(), user, schedule);

    }

    private void validateEnums() {
        validateCounselingField();
        validateCounselingType();
    }

    private void validateCounselingField() {
        if (counselingField == null || isValidEnum(CounselingField.class, counselingField.name())) {
            throw new ConsultationException(ConsultationErrorCode.INVALID_COUNSELING_FIELD);
        }
    }

    private void validateCounselingType() {
        if (counselingType == null ||isValidEnum(CounselingType.class, counselingType.name())) {
            throw new ConsultationException(ConsultationErrorCode.INVALID_COUNSELING_TYPE);
        }
    }

    private <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String enumName) {
        return Arrays.stream(enumClass.getEnumConstants())
                .noneMatch(e -> e.name().equals(enumName));
    }

}
