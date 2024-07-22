package com.example.tnote.boundedContext.schedule.exception;

import com.example.tnote.base.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ScheduleErrorCode implements ErrorCode {
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "schedule를 찾을 수 없습니다."),
    INVALID_SCHEDULE(HttpStatus.BAD_REQUEST, "유효한 학기가 아닙니다.");
    private final HttpStatus httpStatus;
    private final String message;

    ScheduleErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return "";
    }
}
