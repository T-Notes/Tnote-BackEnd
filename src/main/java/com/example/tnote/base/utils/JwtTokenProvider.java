package com.example.tnote.base.utils;

import com.example.tnote.base.constant.Constants;
import com.example.tnote.base.exception.JwtException;
import com.example.tnote.base.exception.JwtErrorResult;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.user.dto.Token;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    @Value("${custom.jwt.secret-key}")
    private String SECRET_KEY;

    private SecretKey key;

    private final UserDetailsService userDetailsService;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        String secretKey = Base64.getEncoder().encodeToString(SECRET_KEY.getBytes());
        key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public Token createToken(String email) {
        Claims claims = Jwts.claims().setSubject(email); // JWT payload 에 저장되는 정보단위, 보통 여기서 user를 식별하는 값을 넣는다.
        Date now = new Date();

        String accessToken = Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + Constants.ACCESS_TOKEN_EXPIRE_COUNT)) // 토큰 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 사용할 암호화 알고리즘과 signature 에 들어갈 secret값 세팅
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + Constants.REFRESH_TOKEN_EXPIRE_COUNT))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .key(email)
                .build();
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


//    // 토큰의 유효성 확인
//    public void validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
//        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
//            throw new JwtException(JwtErrorResult.WRONG_TOKEN);
//        } catch (ExpiredJwtException e) {
//            throw new JwtException(JwtErrorResult.EXPIRED_TOKEN);
//        } catch (UnsupportedJwtException e) {
//            throw new JwtException(JwtErrorResult.UNSUPPORTED);
//        } catch (IllegalArgumentException e) {
//            throw new JwtException(JwtErrorResult.WRONG_TOKEN);
//        }
//    }
    public boolean isValidToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(token);

            log.info("expiredDate={}", claimsJws.getBody().getExpiration());
            log.info("expired?={}", claimsJws.getBody().getExpiration().before(new Date()));
            return !claimsJws.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return false;
        } catch (UnsupportedJwtException e) {
            throw new JwtException(JwtErrorResult.UNSUPPORTED);
        } catch (MalformedJwtException | IllegalArgumentException e) {
            throw new JwtException(JwtErrorResult.WRONG_TOKEN);
        } catch (SignatureException e) {
            throw new JwtException(JwtErrorResult.WRONG_SIGNATURE);
        }
    }

    public boolean validateRefreshToken(RefreshToken refreshTokenObj){
        // refresh 객체에서 refreshToken 추출
        String refreshToken = refreshTokenObj.getRefreshToken();

        try {
            // 검증
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(refreshToken);

            if (!claims.getBody().getExpiration().before(new Date())) {
                return true;
            }
        } catch (Exception e) {
            //refresh 토큰이 만료되었을 경우, 로그인이 필요합니다.
            return false;
        }

        return false;
    }

    public String recreationAccessToken(String email){
        Claims claims = Jwts.claims().setSubject(email); // JWT payload 에 저장되는 정보단위
        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + Constants.ACCESS_TOKEN_EXPIRE_COUNT)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
