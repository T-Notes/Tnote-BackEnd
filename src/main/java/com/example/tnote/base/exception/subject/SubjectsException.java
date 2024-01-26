package com.example.tnote.base.exception.subject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SubjectsException extends RuntimeException {
    private final SubjectsErrorResult subjectsErrorResult;
}
