package com.example.tnote.base.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClassLogException extends RuntimeException {
    private final ClassLogErrorResult classLogErrorResult;
}
