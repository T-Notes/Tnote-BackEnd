package com.example.tnote.base.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtException extends RuntimeException {
    private final JwtErrorResult jwtErrorResult;
}