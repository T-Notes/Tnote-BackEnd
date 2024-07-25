package com.example.tnote.boundedContext.proceeding.exception;

import com.example.tnote.base.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProceedingErrorCode implements ErrorCode {
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
