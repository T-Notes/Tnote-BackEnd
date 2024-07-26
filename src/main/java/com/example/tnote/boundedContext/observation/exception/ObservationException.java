package com.example.tnote.boundedContext.observation.exception;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.exception.ErrorCode;

public class ObservationException extends CustomException {
    public ObservationException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
