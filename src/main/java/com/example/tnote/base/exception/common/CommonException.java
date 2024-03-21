package com.example.tnote.base.exception.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class CommonException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;

    public static final CommonException USER_NOT_FOUND = new CommonException(HttpStatus.NOT_FOUND,
            "not found user");
    public static final CommonException BAD_REQUEST = new CommonException(HttpStatus.BAD_REQUEST,
            "bad request");
    public static final CommonException MEMBER_EXISTS = new CommonException(HttpStatus.BAD_REQUEST,
            "member already exists");
    public static final CommonException NO_SUCH_ALGORITHM = new CommonException(HttpStatus.BAD_REQUEST,
            "no such algorithm");
    public static final CommonException UNAUTHORIZED = new CommonException(HttpStatus.UNAUTHORIZED,
            "unauthorized");
}
