package com.example.tnote.base.exception.classLog;

import com.example.tnote.base.exception.classLog.ClassLogErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ClassLogException extends RuntimeException {
    private final ClassLogErrorResult classLogErrorResult;
}
