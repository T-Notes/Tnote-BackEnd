package com.example.tnote.base.exception.todo;

import com.example.tnote.base.exception.todo.TodoErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TodoException extends RuntimeException {

    private final TodoErrorResult todoErrorResult;
}

