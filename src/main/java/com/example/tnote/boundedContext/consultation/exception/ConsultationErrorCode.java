package com.example.tnote.boundedContext.consultation.exception;

import com.example.tnote.base.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ConsultationErrorCode implements ErrorCode {
    CONSULTATION_NOT_FOUNT(HttpStatus.NOT_FOUND, "not found consultation"),
    INVALID_CONSULTATION_DATA(HttpStatus.BAD_REQUEST, "Invalid consultation log data"),
    INVALID_COUNSELING_TYPE(HttpStatus.BAD_REQUEST, "Invalid counseling type"),
    INVALID_COUNSELING_FIELD(HttpStatus.BAD_REQUEST, "Invalid counseling field"),
    INVALID_CONSULTATION_DATE(HttpStatus.BAD_REQUEST, "Consultation date must be within the schedule dates");

    private final HttpStatus httpStatus;
    private final String message;

    ConsultationErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return "";
    }
}
