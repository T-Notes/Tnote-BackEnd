package com.example.tnote.boundedContext.subject.exception;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.exception.ErrorCode;

public class SubjectException extends CustomException {

    public SubjectException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
