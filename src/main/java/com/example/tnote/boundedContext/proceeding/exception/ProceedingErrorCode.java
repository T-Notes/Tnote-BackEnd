package com.example.tnote.boundedContext.proceeding.exception;

import com.example.tnote.base.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProceedingErrorCode implements ErrorCode {
    PROCEEDING_NOT_FOUNT(HttpStatus.NOT_FOUND, "업무일지를 찾을 수 없습니다."),
    INVALID_PROCEEDING_DATE(HttpStatus.BAD_REQUEST, "해당 업무일지 내의 기간이 아닙니다."),
    INVALID_PROCEEDING_DATA(HttpStatus.BAD_REQUEST, "업무일지에 적합한 내용이 아닙니다.")
    ;
    private final HttpStatus httpStatus;
    private final String message;

    ProceedingErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return "";
    }
}
