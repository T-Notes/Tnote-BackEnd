package com.example.tnote.base.utils;

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
}
