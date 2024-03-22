package com.example.tnote.base.exception;


import com.example.tnote.base.exception.classLog.ClassLogErrorResult;
import com.example.tnote.base.exception.classLog.ClassLogException;
import com.example.tnote.base.exception.common.CommonException;
import com.example.tnote.base.exception.jwt.JwtException;
import com.example.tnote.base.exception.schedule.ScheduleException;
import com.example.tnote.base.exception.subject.SubjectsException;
import com.example.tnote.base.exception.todo.TodoException;
import com.example.tnote.base.exception.user.UserException;
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
        return this.makeErrorResponseEntity(exception);
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final CommonException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ErrorResponse(exception.getHttpStatus().name(), exception.getMessage()));
    }

    @ExceptionHandler({JwtException.class})
    public ResponseEntity<ErrorResponse> handleException(final JwtException exception) {
        log.warn("Exception occur: ", exception);
        return this.makeErrorResponseEntity(exception);
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final JwtException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ErrorResponse(exception.getHttpStatus().name(), exception.getMessage()));
    }

    @ExceptionHandler({ClassLogException.class})
    public ResponseEntity<ErrorResponse> handleClassLogException(final ClassLogException exception) {
        log.warn("ClassLogException occur: ", exception);
        return this.makeErrorResponseEntity(exception.getClassLogErrorResult());
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final ClassLogErrorResult errorResult) {
        return ResponseEntity.status(errorResult.getHttpStatus())
                .body(new ErrorResponse(errorResult.name(), errorResult.getMessage()));
    }


    @ExceptionHandler({SubjectsException.class})
    public ResponseEntity<ErrorResponse> handleException(final SubjectsException exception) {
        log.warn("Exception occur: ", exception);
        return this.makeErrorResponseEntity(exception);
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final SubjectsException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ErrorResponse(exception.getHttpStatus().name(), exception.getMessage()));
    }

    @ExceptionHandler({ScheduleException.class})
    public ResponseEntity<ErrorResponse> handleException(final ScheduleException exception) {
        log.warn("Exception occur: ", exception);
        return this.makeErrorResponseEntity(exception);
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final ScheduleException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ErrorResponse(exception.getHttpStatus().name(), exception.getMessage()));
    }

    @ExceptionHandler({TodoException.class})
    public ResponseEntity<ErrorResponse> handleException(final TodoException exception) {
        log.warn("Exception occur: ", exception);
        return this.makeErrorResponseEntity(exception);
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final TodoException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ErrorResponse(exception.getHttpStatus().name(), exception.getMessage()));
    }

    @ExceptionHandler({UserException.class})
    public ResponseEntity<ErrorResponse> handleException(final UserException exception) {
        log.warn("Exception occur: ", exception);
        return this.makeErrorResponseEntity(exception);
    }

    private ResponseEntity<ErrorResponse> makeErrorResponseEntity(final UserException exception) {
        return ResponseEntity.status(exception.getHttpStatus())
                .body(new ErrorResponse(exception.getHttpStatus().name(), exception.getMessage()));
    }


    @Getter
    @RequiredArgsConstructor
    static class ErrorResponse {
        private final String code;
        private final String message;
    }
}
