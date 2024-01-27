package com.example.tnote.base.exception.observation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ObservationException extends RuntimeException{
    private final ObservationErrorResult observationErrorResult;
}
