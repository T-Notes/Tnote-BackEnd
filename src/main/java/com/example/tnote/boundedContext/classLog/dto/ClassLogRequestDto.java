package com.example.tnote.boundedContext.classLog.dto;

import com.example.tnote.base.exception.ClassLogErrorResult;
import com.example.tnote.base.exception.ClassLogException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.user.entity.User;
import java.time.LocalDateTime;
import java.util.ArrayList;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassLogRequestDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String plan;
    private String classContents;
    private String submission;
    private String magnitude;
    private boolean isAllDay;

    public ClassLog toEntity(User user) {
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
                .classLogImage(new ArrayList<>()) // 이미지 리스트는 여기서 초기화
                .build();
    }
    private void validate() {
        if (title == null || title.trim().isEmpty()) {
            throw new ClassLogException(ClassLogErrorResult.INVALID_CLASS_LOG_DATA);
        }
        if (startDate == null) {
            throw new ClassLogException(ClassLogErrorResult.INVALID_CLASS_LOG_DATA);
        }
        if (endDate == null) {
            throw new ClassLogException(ClassLogErrorResult.INVALID_CLASS_LOG_DATA);
        }
        if (endDate.isBefore(startDate)) {
            throw new ClassLogException(ClassLogErrorResult.INVALID_CLASS_LOG_DATA);
        }
    }
}