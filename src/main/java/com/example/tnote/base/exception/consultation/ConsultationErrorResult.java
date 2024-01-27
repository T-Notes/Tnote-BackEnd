package com.example.tnote.base.exception.consultation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ConsultationErrorResult {
    CONSULTATION_NOT_FOUNT(HttpStatus.NOT_FOUND,"not found classLog"),
    INVALID_CONSULTATION_DATA(HttpStatus.BAD_REQUEST, "Invalid class log data"),
    INVALID_COUNSELING_TYPE(HttpStatus.BAD_REQUEST,"Invalid counseling type"),
    INVALID_COUNSELING_FIELD(HttpStatus.BAD_REQUEST,"Invalid counseling field");

    private final HttpStatus httpStatus;
    private final String message;
}
