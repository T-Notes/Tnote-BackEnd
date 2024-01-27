package com.example.tnote.base.exception.consultation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ConsultationException extends RuntimeException{
    private final ConsultationErrorResult consultationErrorResult;
}
