package com.example.tnote.boundedContext.RefreshToken.repository;

import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refreshToken);

    boolean existsByKeyEmail(String email);

    void deleteByKeyEmail(String email);

    Optional<RefreshToken> findByEmail(String email);
}
