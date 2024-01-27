package com.example.tnote.base.exception.observation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ObservationErrorResult {
    OBSERVATION_NOT_FOUNT(HttpStatus.NOT_FOUND,"not found observation"),
    INVALID_OBSERVATION_DATA(HttpStatus.BAD_REQUEST, "Invalid observation data");

    private final HttpStatus httpStatus;
    private final String message;
}
