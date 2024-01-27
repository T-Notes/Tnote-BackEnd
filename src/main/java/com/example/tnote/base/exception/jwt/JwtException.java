package com.example.tnote.base.exception.jwt;

import com.example.tnote.base.exception.jwt.JwtErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class JwtException extends RuntimeException {
    private final JwtErrorResult jwtErrorResult;
}