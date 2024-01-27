package com.example.tnote.base.exception.subject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SubjectsErrorResult {

    SUBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "subject is not found"),
    TODAY_IS_WRONG_WITH_DATE(HttpStatus.NOT_FOUND, "오늘과 선택하신 날짜가 다릅니다"),
    SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "schedule is not found");

    private final HttpStatus httpStatus;
    private final String message;
}
