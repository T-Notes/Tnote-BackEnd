package com.example.tnote.base.filter;

import com.example.tnote.base.exception.jwt.JwtException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            Map<String, String> map = new HashMap<>();
            map.put("code", String.valueOf(e.getJwtErrorResult()));
            map.put("message", e.getJwtErrorResult().getMessage());

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(e.getJwtErrorResult().getHttpStatus().value());
            response.getWriter().print(objectMapper.writeValueAsString(map));
        }
    }
}
