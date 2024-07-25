package com.example.tnote.base.exception;

import lombok.Getter;

@Getter
public class CustomExceptions extends RuntimeException{
    private final ErrorCodes errorCodes;

    public CustomExceptions(ErrorCodes errorCodes) {
        super(errorCodes.getMessage());
        this.errorCodes = errorCodes;
    }
}
