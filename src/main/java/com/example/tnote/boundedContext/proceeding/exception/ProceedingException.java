package com.example.tnote.boundedContext.proceeding.exception;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.exception.ErrorCode;

public class ProceedingException extends CustomException {
    public ProceedingException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
