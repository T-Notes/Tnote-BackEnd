package com.example.tnote.boundedContext.RefreshToken.repository;

import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
    boolean existsByKeyEmail(String email);
    void deleteByKeyEmail(String email);
}
