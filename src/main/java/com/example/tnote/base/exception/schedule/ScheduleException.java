package com.example.tnote.base.exception.schedule;

import com.example.tnote.base.exception.schedule.ScheduleErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ScheduleException extends RuntimeException {
    private final ScheduleErrorResult scheduleErrorResult;
}
