package com.example.tnote.boundedContext.observation.exception;

import com.example.tnote.base.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ObservationErrorCode implements ErrorCode {
    OBSERVATION_NOT_FOUNT(HttpStatus.NOT_FOUND, "not found observation"),
    INVALID_OBSERVATION_DATA(HttpStatus.BAD_REQUEST, "Invalid observation data"),
    INVALID_OBSERVATION_DATE(HttpStatus.BAD_REQUEST, "Observation date must be within the schedule dates");
    private final HttpStatus httpStatus;
    private final String message;

    ObservationErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return "";
    }
}
