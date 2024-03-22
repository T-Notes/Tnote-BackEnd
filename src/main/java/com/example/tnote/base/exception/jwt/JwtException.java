package com.example.tnote.base.exception.jwt;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class JwtException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;


    public static final JwtException WRONG_TOKEN = new JwtException(UNAUTHORIZED,
            "wrong token");
    public static final JwtException WRONG_REFRESH_TOKEN = new JwtException(UNAUTHORIZED,
            "wrong refresh token");
    public static final JwtException WRONG_SIGNATURE = new JwtException(UNAUTHORIZED,
            "wrong signature");
    public static final JwtException UNSUPPORTED = new JwtException(UNAUTHORIZED,
            "unsupported token");
    public static final JwtException EXPIRED_REFRESH_TOKEN = new JwtException(UNAUTHORIZED,
            "expired refresh token");
    public static final JwtException EXPIRED_ACCESS_TOKEN = new JwtException(UNAUTHORIZED,
            "expired access token");
    public static final JwtException NOT_FOUND_TOKEN = new JwtException(UNAUTHORIZED,
            "not found token");
    public static final JwtException NOT_ACCESS_TOKEN = new JwtException(UNAUTHORIZED,
            "this is not access token but refresh token");
    public static final JwtException NOT_EXPIRED_TOKEN = new JwtException(UNAUTHORIZED,
            "token is not expired");
}