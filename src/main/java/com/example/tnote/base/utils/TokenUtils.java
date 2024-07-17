package com.example.tnote.base.utils;

import com.example.tnote.base.exception.CustomExceptions;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
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
            throw CustomExceptions.WRONG_TOKEN;
        }
        return user;
    }
}
