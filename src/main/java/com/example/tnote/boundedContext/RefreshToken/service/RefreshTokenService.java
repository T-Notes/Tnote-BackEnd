package com.example.tnote.boundedContext.RefreshToken.service;

import com.example.tnote.base.exception.CommonErrorResult;
import com.example.tnote.base.exception.CommonException;
import com.example.tnote.base.exception.JwtErrorResult;
import com.example.tnote.base.exception.JwtException;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.RefreshToken.repository.RefreshTokenRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public RefreshToken save(String refreshToken, String email) {
        Optional<User> opUser = userRepository.findByEmail(email);
        if(opUser.isEmpty()) {
            throw new CommonException(CommonErrorResult.BAD_REQUEST);
        }

        // 기존의 만료된 리프레시 토큰 삭제
        if(refreshTokenRepository.existsByKeyEmail(email)) {
            refreshTokenRepository.deleteByKeyEmail(email);
        }

        RefreshToken rt = RefreshToken.builder()
                .keyEmail(email)
                .refreshToken(refreshToken)
                .build();

        return refreshTokenRepository.save(rt);
    }

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtException(JwtErrorResult.NOT_FOUND_TOKEN));
    }
}