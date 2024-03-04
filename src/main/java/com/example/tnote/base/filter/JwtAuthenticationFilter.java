package com.example.tnote.base.filter;

import com.example.tnote.base.exception.jwt.JwtException;
import com.example.tnote.base.utils.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String PREFIX_TOKEN = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);
        try {
            if (token != null && jwtTokenProvider.isValidToken(token)) {
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 여기서 인증 객체를 출력해 확인
                log.info("Authentication Object: {}", authentication);
                log.info("Principal Details: {}", authentication.getPrincipal());
            }
        } catch (JwtException e) {
            log.warn("JWT Error: {}", e.getJwtErrorResult().getMessage());
            request.setAttribute("JwtException", e.getJwtErrorResult().getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(PREFIX_TOKEN)) {
            return bearerToken.substring(PREFIX_TOKEN.length());
        }
        return null;
    }
}
