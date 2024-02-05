package com.example.tnote.boundedContext.consultation.dto;

import com.example.tnote.base.exception.consultation.ConsultationErrorResult;
import com.example.tnote.base.exception.consultation.ConsultationException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsultationRequestDto {
    private String studentName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CounselingField counselingField;
    private CounselingType counselingType;
    private String consultationContents;
    private String consultationResult;
    private boolean isAllDay;

    public void validateEnums() {
        validateCounselingField();
        validateCounselingType();
    }

    private void validateCounselingField() {
        if (counselingField == null || EnumUtils.isValidEnum(CounselingField.class, counselingField.name())) {
            throw new ConsultationException(ConsultationErrorResult.INVALID_COUNSELING_FIELD);
        }
    }

    private void validateCounselingType() {
        if (counselingType == null || EnumUtils.isValidEnum(CounselingType.class, counselingType.name())) {
            throw new ConsultationException(ConsultationErrorResult.INVALID_COUNSELING_TYPE);
        }
    }

    static class EnumUtils {
        public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String enumName) {
            return Arrays.stream(enumClass.getEnumConstants())
                    .noneMatch(e -> e.name().equals(enumName));
        }
    }

    public Consultation toEntity(User user) {
        return Consultation.builder()
                .user(user)
                .studentName(this.studentName)
                .startDate(DateUtils.adjustStartDateTime(this.startDate, this.isAllDay))
                .endDate(DateUtils.adjustEndDateTime(this.startDate, this.isAllDay))
                .counselingField(this.counselingField)
                .counselingType(this.counselingType)
                .consultationContents(this.consultationContents)
                .consultationResult(this.consultationResult)
                .build();
    }


}
