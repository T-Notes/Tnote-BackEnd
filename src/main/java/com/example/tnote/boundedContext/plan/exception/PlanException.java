package com.example.tnote.boundedContext.plan.exception;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.exception.ErrorCode;

public class PlanException extends CustomException {
    public PlanException(final ErrorCode errorCode) {
        super(errorCode);
    }
}

