package com.example.tnote.base.config;

import com.example.tnote.base.filter.JwtAuthenticationFilter;
import com.example.tnote.base.filter.JwtExceptionFilter;
import com.example.tnote.base.handler.JwtAccessDeniedHandler;
import com.example.tnote.base.handler.JwtAuthenticationEntryPoint;
import com.example.tnote.base.utils.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtExceptionFilter jwtExceptionFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //http.cors();

        http
                .csrf(
                        AbstractHttpConfigurer::disable
                )
                .headers(headerConfig ->
                        headerConfig.frameOptions(
                                HeadersConfigurer.FrameOptionsConfig::disable
                        )
                )
                .exceptionHandling(exceptionConfig ->
                        exceptionConfig
                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers("/**", "/classLog/**", "/user/**").permitAll()
                                .anyRequest().authenticated()
                )
                .cors(
                        Customizer.withDefaults()
                )
                //세션 정책 설정
                .sessionManagement(configurer ->
                        configurer.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )

                )
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider)
                        , UsernamePasswordAuthenticationFilter.class
                )
                .addFilterBefore(
                        jwtExceptionFilter, JwtAuthenticationFilter.class
                )
                .logout(logout ->
                        logout.logoutSuccessUrl("/")
                )
                .oauth2Login(oauth2 ->
                        oauth2.redirectionEndpoint(info ->
                                info.baseUri("/oauth2/code/*")

                        )
                )

        ;

        return http.build();
    }
}
