package com.example.tnote.base.exception.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum JwtErrorResult {

    WRONG_TOKEN(HttpStatus.UNAUTHORIZED, "wrong token"),
    WRONG_SIGNATURE(HttpStatus.UNAUTHORIZED, "wrong signature"),
    UNSUPPORTED(HttpStatus.UNAUTHORIZED, "unsupported token"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "expired access token"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "expired access token"),
    NOT_FOUND_TOKEN(HttpStatus.UNAUTHORIZED, "not found token"),
    NOT_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "this is not access token but refresh token"),
    NOT_EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "token is not expired");

    private final HttpStatus httpStatus;
    private final String message;
}