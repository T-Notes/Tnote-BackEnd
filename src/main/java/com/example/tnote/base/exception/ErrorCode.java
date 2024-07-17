package com.example.tnote.base.exception;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String CODE_PREFIX = "TN";

    String getCode();
    String name();
    HttpStatus getHttpStatus();
    String getMessage();
}
