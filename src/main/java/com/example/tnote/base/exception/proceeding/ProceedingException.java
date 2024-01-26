package com.example.tnote.base.exception.proceeding;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProceedingException extends RuntimeException{
    private final ProceedingErrorResult proceedingErrorResult;
}
