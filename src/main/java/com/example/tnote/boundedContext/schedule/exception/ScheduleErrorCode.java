package com.example.tnote.boundedContext.schedule.exception;

import com.example.tnote.base.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ScheduleErrorCode implements ErrorCode {
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "schedule를 찾을 수 없습니다."),
    INVALID_SCHEDULE(HttpStatus.BAD_REQUEST, "유효한 학기가 아닙니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "refresh token이 유효하지 않습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "refresh token의 만료 기간이 지났습니다.");
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
