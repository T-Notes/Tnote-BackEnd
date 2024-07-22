package com.example.tnote.boundedContext.user.exception;

import com.example.tnote.base.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."),
    INVALID_USER(HttpStatus.BAD_REQUEST, "유효한 유저가 아닙니다.");
    private final HttpStatus httpStatus;
    private final String message;

    UserErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return "";
    }
}
