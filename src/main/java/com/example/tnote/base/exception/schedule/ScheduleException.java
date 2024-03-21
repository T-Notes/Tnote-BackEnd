package com.example.tnote.base.exception.schedule;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class ScheduleException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public static final ScheduleException SUBJECT_NOT_FOUND = new ScheduleException(HttpStatus.NOT_FOUND,
            "subject is not found");
    public static final ScheduleException SCHEDULE_NOT_FOUND = new ScheduleException(HttpStatus.NOT_FOUND,
            "schedule is not found");
}
