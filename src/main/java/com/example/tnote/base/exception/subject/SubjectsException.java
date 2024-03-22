package com.example.tnote.base.exception.subject;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class SubjectsException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public static final SubjectsException SUBJECT_NOT_FOUND = new SubjectsException(HttpStatus.NOT_FOUND,
            "subject is not found");
    public static final SubjectsException SCHEDULE_NOT_FOUND = new SubjectsException(HttpStatus.NOT_FOUND,
            "schedule is not found");
    public static final SubjectsException TODAY_IS_WRONG_WITH_DATE = new SubjectsException(HttpStatus.NOT_FOUND,
            "오늘과 선택하신 날짜가 다릅니다");
}
