package com.example.tnote.base.exception.classLog;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum ClassLogErrorResult {
    CLASS_LOG_NOT_FOUNT(HttpStatus.NOT_FOUND,"not found classLog"),
    INVALID_CLASS_LOG_DATA(HttpStatus.BAD_REQUEST, "Invalid class log data");

    private final HttpStatus httpStatus;
    private final String message;
}