package com.example.tnote.base.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TodoException extends RuntimeException {

    private final TodoErrorResult todoErrorResult;
}

