package com.example.tnote.boundedContext.subject.exception;

import com.example.tnote.base.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SubjectErrorCode implements ErrorCode {
    SUBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "과목를 찾을 수 없습니다."),
    WRONG_CLASS_TIME(HttpStatus.BAD_REQUEST, "학습 일지 날짜가 틀립니다."),
    INVALID_SUBJECT(HttpStatus.BAD_REQUEST, "유효한 과목이 아닙니다.");
    private final HttpStatus httpStatus;
    private final String message;

    SubjectErrorCode(final HttpStatus httpStatus, final String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    @Override
    public String getCode() {
        return "";
    }
}
