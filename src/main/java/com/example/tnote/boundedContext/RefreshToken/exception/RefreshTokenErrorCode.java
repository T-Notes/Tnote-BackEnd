package com.example.tnote.boundedContext.RefreshToken.exception;

import com.example.tnote.base.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum RefreshTokenErrorCode implements ErrorCode {
    TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "토큰를 찾을 수 없습니다."),
    UNSUPPORTED(HttpStatus.NOT_FOUND, "unsupported"),
    WRONG_SIGNATURE(HttpStatus.BAD_REQUEST, "유효하지 않 시그니쳐입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "refresh token이 유효하지 않습니다."),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "refresh token의 만료 기간이 지났습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "access token이 유효하지 않습니다."),
    EXPIRED_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "access token의 만료 기간이 지났습니다.");
    private final HttpStatus httpStatus;
    private final String message;

    RefreshTokenErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return "";
    }
}
