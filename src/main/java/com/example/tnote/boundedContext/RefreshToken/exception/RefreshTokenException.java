package com.example.tnote.boundedContext.RefreshToken.exception;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.exception.ErrorCode;

public class RefreshTokenException extends CustomException {

    public RefreshTokenException(final ErrorCode errorCode) {
        super(errorCode);
    }
}
