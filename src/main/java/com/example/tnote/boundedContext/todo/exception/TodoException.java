package com.example.tnote.boundedContext.todo.exception;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.exception.ErrorCode;

public class TodoException extends CustomException {

    public TodoException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
