package com.example.tnote.boundedContext.classLog.dto;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.exception.ClassLogErrorCode;
import com.example.tnote.boundedContext.classLog.exception.ClassLogException;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Getter;

@Getter
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

    public ClassLogSaveRequest(final String color, final boolean isAllDay, final String magnitude,
                               final String submission, final String classContents,
                               final String plan, final LocalDateTime endDate,
                               final LocalDateTime startDate, final String title) {
        this.color = color;
        this.isAllDay = isAllDay;
        this.magnitude = magnitude;
        this.submission = submission;
        this.classContents = classContents;
        this.plan = plan;
        this.endDate = endDate;
        this.startDate = startDate;
        this.title = title;
    }

    public ClassLog toEntity(final User user, final Schedule schedule) {
        validate();
        return new ClassLog(this.title, this.startDate, this.endDate, this.plan,
                this.classContents, this.submission, this.magnitude,
                this.color, user, schedule, new ArrayList<>());
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