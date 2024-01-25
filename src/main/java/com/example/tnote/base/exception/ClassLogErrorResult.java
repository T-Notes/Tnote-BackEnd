package com.example.tnote.base.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum ClassLogErrorResult {
    CLASS_LOG_NOT_FOUNT(HttpStatus.NOT_FOUND,"not found classLog");
    private final HttpStatus httpStatus;
    private final String message;
}
