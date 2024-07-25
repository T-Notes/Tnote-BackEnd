package com.example.tnote.boundedContext.consultation.exception;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.exception.ErrorCode;

public class ConsultationException extends CustomException {
    public ConsultationException(ErrorCode errorCode) {
        super(errorCode);
    }
}
