package com.example.tnote.base.exception.user;

import com.example.tnote.base.exception.user.UserErrorResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserException extends RuntimeException {
    private final UserErrorResult userErrorResult;
}
