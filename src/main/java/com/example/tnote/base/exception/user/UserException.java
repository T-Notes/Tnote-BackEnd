package com.example.tnote.base.exception.user;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final String message;

    public static final UserException DUPLICATED_NICKNAME = new UserException(BAD_REQUEST,
            "duplicated nickname");
    public static final UserException NOT_VALID_FIELD = new UserException(BAD_REQUEST,
            "field is not valid");
    public static final UserException WRONG_USRE = new UserException(BAD_REQUEST,
            "wrong user");
    public static final UserException ALREADY_EXISTS_ROLE = new UserException(BAD_REQUEST,
            "already role exists");
    public static final UserException WRONG_EMAIL = new UserException(BAD_REQUEST,
            "wrong mail");
    public static final UserException USER_NOT_FOUND = new UserException(NOT_FOUND,
            "user is not found");
}
