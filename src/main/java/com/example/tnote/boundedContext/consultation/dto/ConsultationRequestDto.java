package com.example.tnote.boundedContext.consultation.dto;

import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
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
            throw new IllegalArgumentException("Invalid counseling field: " + counselingField);
        }
    }

    private void validateCounselingType() {
        if (counselingType == null || EnumUtils.isValidEnum(CounselingType.class, counselingType.name())) {
            throw new IllegalArgumentException("Invalid counseling type: " + counselingType);
        }
    }

    private static class EnumUtils {
        public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String enumName) {
            return Arrays.stream(enumClass.getEnumConstants())
                    .noneMatch(e -> e.name().equals(enumName));
        }
    }


}
