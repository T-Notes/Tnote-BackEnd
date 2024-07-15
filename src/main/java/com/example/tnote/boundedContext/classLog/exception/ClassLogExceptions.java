package com.example.tnote.boundedContext.classLog.exception;

import com.example.tnote.base.exception.CustomExceptions;
import com.example.tnote.base.exception.ErrorCode;

public class ClassLogExceptions extends CustomExceptions {

    public ClassLogExceptions(final ErrorCode errorCode) {
        super(errorCode);
    }
}
