package com.example.tnote.base.utils;

import static com.example.tnote.boundedContext.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.exception.UserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class TokenUtils {
    public static PrincipalDetails checkValidToken(PrincipalDetails user) {
        if (user == null) {
            log.warn("PrincipalDetails is null");
            throw new UserException(USER_NOT_FOUND);
        }
        return user;
    }
}
