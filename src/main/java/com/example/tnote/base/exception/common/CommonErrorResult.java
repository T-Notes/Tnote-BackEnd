package com.example.tnote.base.exception.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CommonErrorResult {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "not found user"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST,"bad request"),
    MEMBER_EXISTS(HttpStatus.BAD_REQUEST,"member already exists"),
    NO_SUCH_ALGORITHM(HttpStatus.BAD_REQUEST,"no such algorithm"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"unauthorized");

    private final HttpStatus httpStatus;
    private final String message;
}
