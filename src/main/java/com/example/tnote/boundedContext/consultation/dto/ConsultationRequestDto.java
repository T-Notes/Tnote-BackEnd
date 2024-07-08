package com.example.tnote.boundedContext.consultation.dto;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsultationRequestDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CounselingField counselingField;
    private CounselingType counselingType;
    private String consultationContents;
    private String consultationResult;
    private boolean isAllDay;
    private String color;

    public void validateEnums() {
        validateCounselingField();
        validateCounselingType();
    }

    private void validateCounselingField() {
        if (counselingField == null || EnumUtils.isValidEnum(CounselingField.class, counselingField.name())) {
            throw CustomException.INVALID_COUNSELING_FIELD;
        }
    }

    private void validateCounselingType() {
        if (counselingType == null || EnumUtils.isValidEnum(CounselingType.class, counselingType.name())) {
            throw CustomException.INVALID_COUNSELING_TYPE;
        }
    }

    static class EnumUtils {
        public static <E extends Enum<E>> boolean isValidEnum(Class<E> enumClass, String enumName) {
            return Arrays.stream(enumClass.getEnumConstants())
                    .noneMatch(e -> e.name().equals(enumName));
        }
    }

    public Consultation toEntity(User user, Schedule schedule) {
        return Consultation.builder()
                .user(user)
                .title(this.title)
                .startDate(DateUtils.adjustStartDateTime(this.startDate, this.isAllDay))
                .endDate(DateUtils.adjustEndDateTime(this.endDate, this.isAllDay))
                .counselingField(this.counselingField)
                .counselingType(this.counselingType)
                .consultationContents(this.consultationContents)
                .consultationResult(this.consultationResult)
                .consultationImage(new ArrayList<>())
                .schedule(schedule)
                .color(this.color)
                .build();
    }


}
