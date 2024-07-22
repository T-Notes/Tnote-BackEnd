package com.example.tnote.boundedContext.user.exception;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.exception.ErrorCode;

public class UserException extends CustomException {

    public UserException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
