package com.example.tnote.base.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (JwtException e) {
            Map<String, String> map = new HashMap<>();
            map.put("code", e.getClass().getSimpleName());
            map.put("message", e.getMessage());

            StackTraceElement element = e.getStackTrace()[0];
            log.warn("[{}] occurs caused by {}.{}() {} line : {}", e.getClass().getSimpleName(), element.getClassName(),
                    element.getMethodName(), element.getLineNumber(), e.getMessage());

            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().print(objectMapper.writeValueAsString(map));
        }
    }
}
