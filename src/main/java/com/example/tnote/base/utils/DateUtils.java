package com.example.tnote.base.utils;

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
}
