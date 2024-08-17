package com.example.tnote.boundedContext.classLog.dto;

import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.exception.ClassLogErrorCode;
import com.example.tnote.boundedContext.classLog.exception.ClassLogException;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassLogSaveRequest {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String plan;
    private String classContents;
    private String submission;
    private String magnitude;
    private boolean isAllDay;
    private String color;

    public ClassLog toEntity(User user, Schedule schedule) {
        validate();
        return ClassLog.builder()
                .user(user)
                .title(this.title)
                .startDate(DateUtils.adjustStartDateTime(this.startDate, this.isAllDay))
                .endDate(DateUtils.adjustEndDateTime(this.endDate, this.isAllDay))
                .classContents(this.classContents)
                .plan(this.plan)
                .submission(this.submission)
                .magnitude(this.magnitude)
                .classLogImage(new ArrayList<>())
                .schedule(schedule)
                .color(this.color)
                .build();
    }

    private void validate() {
        if (title == null || title.trim().isEmpty()) {
            throw new ClassLogException(ClassLogErrorCode.INVALID_CLASS_LOG_DATA);
        }
        if (startDate == null) {
            throw new ClassLogException(ClassLogErrorCode.INVALID_CLASS_LOG_DATA);
        }
        if (endDate == null) {
            throw new ClassLogException(ClassLogErrorCode.INVALID_CLASS_LOG_DATA);
        }
        if (endDate.isBefore(startDate)) {
            throw new ClassLogException(ClassLogErrorCode.INVALID_CLASS_LOG_DATA);
        }
    }
}