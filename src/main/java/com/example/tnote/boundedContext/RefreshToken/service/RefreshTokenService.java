package com.example.tnote.boundedContext.RefreshToken.service;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.RefreshToken.repository.RefreshTokenRepository;
import com.example.tnote.boundedContext.user.repository.UserRepository;
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
    public RefreshToken save(String refreshToken, String email) {
        userRepository.findByEmail(email)
                .orElseThrow(() -> CustomException.BAD_REQUEST);

        // 기존의 만료된 리프레시 토큰 삭제
        if (refreshTokenRepository.existsByKeyEmail(email)) {
            refreshTokenRepository.deleteByKeyEmail(email);
        }

        RefreshToken rt = RefreshToken.builder()
                .keyEmail(email)
                .refreshToken(refreshToken)
                .build();

        return refreshTokenRepository.save(rt);
    }

    @Transactional(readOnly = true)
    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> CustomException.WRONG_REFRESH_TOKEN);
    }
}