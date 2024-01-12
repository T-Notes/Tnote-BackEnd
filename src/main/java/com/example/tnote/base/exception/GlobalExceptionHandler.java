package com.example.tnote.base.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler({CommonException.class})
    public ResponseEntity<ErrorResponse> handleException(final CommonException exception) {
        log.warn("Exception occur: ", exception);
        return this.makeErrorResponseEntity(exception.getCommonErrorResult());
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final CommonErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
    }

    @ExceptionHandler({JwtException.class})
    public ResponseEntity<ErrorResponse> handleException(final JwtException exception) {
        log.warn("Exception occur: ", exception);
        return this.makeErrorResponseEntity(exception.getJwtErrorResult());
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final JwtErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
    }

    @ExceptionHandler({SubjectsException.class})
    public ResponseEntity<ErrorResponse> handleException(final SubjectsException exception) {
        log.warn("Exception occur: ", exception);
        return this.makeErrorResponseEntity(exception.getSubjectsErrorResult());
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final SubjectsErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
    }

    @ExceptionHandler({ScheduleException.class})
    public ResponseEntity<ErrorResponse> handleException(final ScheduleException exception) {
        log.warn("Exception occur: ", exception);
        return this.makeErrorResponseEntity(exception.getScheduleErrorResult());
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final ScheduleErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
    }

    @ExceptionHandler({TodoException.class})
    public ResponseEntity<ErrorResponse> handleException(final TodoException exception) {
        log.warn("Exception occur: ", exception);
        return this.makeErrorResponseEntity(exception.getTodoErrorResult());
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final TodoErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
    }

    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String code;
        private final String message;
    }
}
