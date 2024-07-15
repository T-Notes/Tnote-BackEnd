package com.example.tnote.base.utils;

import static com.example.tnote.base.exception.ErrorCode.JWT_ERROR;

import com.example.tnote.base.exception.CustomException;
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
            throw new CustomException(JWT_ERROR, "jwt가 올바르지 않습니다");
        }
        return user;
    }
}
