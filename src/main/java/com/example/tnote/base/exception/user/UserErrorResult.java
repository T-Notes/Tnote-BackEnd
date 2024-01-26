package com.example.tnote.base.exception.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorResult {

    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "duplicated nickname"),
    NOT_VALID_FIELD(HttpStatus.BAD_REQUEST, "field is not valid"),
    WRONG_USRE(HttpStatus.BAD_REQUEST, "wrong user"),
    ALREADY_EXISTS_ROLE(HttpStatus.BAD_REQUEST, "already role exists"),
    WRONG_EMAIL(HttpStatus.BAD_REQUEST, "작성하신 메일이 틀렸습니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "user is not found");

    private final HttpStatus httpStatus;
    private final String message;
}
