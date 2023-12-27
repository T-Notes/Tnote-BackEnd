package com.example.tnote.boundedContext.RefreshToken.entity;

import com.example.tnote.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@Table(name = "REFRESH_TOKEN_TB")
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_id", nullable = false)
    private Long refreshTokenId;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "key_email", nullable = false)
    private String keyEmail;
}
