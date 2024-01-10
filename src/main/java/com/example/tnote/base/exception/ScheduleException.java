package com.example.tnote.base.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleException extends RuntimeException {
    private final ScheduleErrorResult scheduleErrorResult;
}
