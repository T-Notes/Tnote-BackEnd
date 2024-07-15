package com.example.tnote.base.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        log.warn("[Exception] occurs : ", exception);
        return ResponseEntity.internalServerError()
                .body(new ErrorResponse("UNEXPECTED_ERROR", "some errors occurred : " + exception.getMessage()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException exception) {
        ErrorCodes errorCode = exception.getErrorCode();
        StackTraceElement element = exception.getStackTrace()[0];
        log.warn("[{}] occurs caused by {}.{}() {} line : {}", errorCode.name(), element.getClassName(),
                element.getMethodName(), element.getLineNumber(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus())
                .body(new ErrorResponse(errorCode.name(), errorCode.getMessage()));
    }

    public record ErrorResponse(String code, String message) {
    }
}