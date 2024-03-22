package com.example.tnote.base.exception.todo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class TodoException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;

    public static final TodoException TODO_NOT_FOUND = new TodoException(HttpStatus.NOT_FOUND,
            "todo is not found");
    public static final TodoException SCHEDULE_NOT_FOUND = new TodoException(HttpStatus.NOT_FOUND,
            "schedule is not found");
}

