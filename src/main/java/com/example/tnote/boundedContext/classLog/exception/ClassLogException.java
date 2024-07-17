package com.example.tnote.boundedContext.classLog.exception;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.exception.ErrorCode;

public class ClassLogException extends CustomException {

    public ClassLogException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
