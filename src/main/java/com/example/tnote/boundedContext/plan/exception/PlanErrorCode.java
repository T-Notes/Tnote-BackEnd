package com.example.tnote.boundedContext.plan.exception;

import com.example.tnote.base.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PlanErrorCode implements ErrorCode {

    INVALID_PLAN_DATE(HttpStatus.BAD_REQUEST, "해당 기간에 일치하는 일정이 존재하지 않습니다.");
    private final HttpStatus httpStatus;
    private final String message;

    PlanErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return "";
    }
}
