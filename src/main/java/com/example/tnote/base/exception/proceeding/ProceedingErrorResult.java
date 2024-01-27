package com.example.tnote.base.exception.proceeding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum ProceedingErrorResult {
    PROCEEDING_NOT_FOUNT(HttpStatus.NOT_FOUND,"not found proceeding"),
    INVALID_PROCEEDING_DATA(HttpStatus.BAD_REQUEST, "Invalid proceeding data");

    private final HttpStatus httpStatus;
    private final String message;
}
