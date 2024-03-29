package com.example.tnote.base.filter;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.utils.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String PREFIX_TOKEN = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = resolveToken(request);
        try {
            if (token != null) {
                if (!jwtTokenProvider.isValidToken(token)) {
                    throw CustomException.WRONG_TOKEN;
                }
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 여기서 인증 객체를 출력해 확인
                log.info("Authentication Object: {}", authentication);
                log.info("Principal Details: {}", authentication.getPrincipal());
            }
        } catch (CustomException e) {
            log.warn("JWT Error: {}", e.getMessage());
            request.setAttribute("JwtException", e.getMessage());
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
