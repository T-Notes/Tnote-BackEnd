package com.example.tnote.boundedContext.schedule.exception;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.exception.ErrorCode;

public class ScheduleException extends CustomException {

    public ScheduleException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
