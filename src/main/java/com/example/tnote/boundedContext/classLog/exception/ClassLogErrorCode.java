package com.example.tnote.boundedContext.classLog.exception;

import com.example.tnote.base.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ClassLogErrorCode implements ErrorCode {
    CLASS_LOG_NOT_FOUNT(HttpStatus.NOT_FOUND, "학급일지를 찾을 수 없습니다."),
    INVALID_CLASS_LOG_DATA(HttpStatus.BAD_REQUEST, "올바른 요청 방식이 아닙니다."),
    INVALID_CLASS_LOG_DATE(HttpStatus.BAD_REQUEST, "해당 기간에 일치하는 학급일지가 존재하지 않습니다.");
    private final HttpStatus httpStatus;
    private final String message;

    ClassLogErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return "";
    }
}
