package com.example.tnote.boundedContext.RefreshToken.service;

import static com.example.tnote.base.exception.ErrorCode.DATA_NOT_FOUND;
import static com.example.tnote.base.exception.ErrorCode.JWT_ERROR;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.RefreshToken.repository.RefreshTokenRepository;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public RefreshToken save(String refreshToken, String email, Long expirationMs) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(DATA_NOT_FOUND, "user data가 없습니다."));

        // 기존의 만료된 리프레시 토큰 삭제
        if (refreshTokenRepository.existsByKeyEmail(email)) {
            refreshTokenRepository.deleteByKeyEmail(email);
        }

        RefreshToken rt = RefreshToken.builder()
                .keyEmail(email)
                .refreshToken(refreshToken)
                .expiration(LocalDateTime.now().plusSeconds(expirationMs / 1000))
                .build();

        return refreshTokenRepository.save(rt);
    }

    @Transactional(readOnly = true)
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new CustomException(JWT_ERROR, "refresh token이 유효하지 않습니다."));
    }
}