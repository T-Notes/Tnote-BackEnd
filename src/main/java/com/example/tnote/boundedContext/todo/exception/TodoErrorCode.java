package com.example.tnote.boundedContext.todo.exception;

import com.example.tnote.base.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TodoErrorCode implements ErrorCode {
    TODO_NOT_FOUND(HttpStatus.NOT_FOUND, "todo를 찾을 수 없습니다."),
    INVALID_TODO(HttpStatus.BAD_REQUEST, "유효한 todo가 아닙니다.");
    private final HttpStatus httpStatus;
    private final String message;

    TodoErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return "";
    }
}
