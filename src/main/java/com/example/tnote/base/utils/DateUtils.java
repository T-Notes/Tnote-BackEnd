package com.example.tnote.base.utils;

import com.example.tnote.boundedContext.archive.constant.DateType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DateUtils {
    public static LocalDateTime adjustStartDateTime(LocalDateTime startDate, boolean isAllDay) {
        if (isAllDay) {
            return startDate.withHour(12).withMinute(0);
        }
        return startDate;
    }

    public static LocalDateTime adjustEndDateTime(LocalDateTime endDate, boolean isAllDay) {
        if (isAllDay) {
            return endDate.withHour(23).withMinute(59);
        }
        return endDate;
    }

    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date.atStartOfDay();
    }

    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date.atTime(23, 59, 59);
    }

    public static LocalDate calculateStartDate(DateType dateType) {
        LocalDate now = LocalDate.now();

        if (dateType == DateType.ALL) {
            return LocalDate.of(1970, 1, 1); // Epoch 시작 날짜로 설정
        }
        if (dateType == DateType.ONE_DAY) {
            return now.minusDays(1);
        }
        if (dateType == DateType.ONE_WEEK) {
            return now.minusWeeks(1);
        }
        if (dateType == DateType.ONE_MONTH) {
            return now.minusMonths(1);
        }
        if (dateType == DateType.SIX_MONTH) {
            return now.minusMonths(6);
        }
        if (dateType == DateType.ONE_YEAR) {
            return now.minusYears(1);
        }
        throw new IllegalArgumentException("Unsupported DateType: " + dateType);
    }
}
