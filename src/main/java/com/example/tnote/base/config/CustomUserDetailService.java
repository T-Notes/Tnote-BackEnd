package com.example.tnote.base.config;

import com.example.tnote.base.exception.UserException;
import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static com.example.tnote.base.exception.UserErrorResult.USER_NOT_FOUND;

@RequiredArgsConstructor
@Slf4j
@Component
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public PrincipalDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // todo 예외처리 custom 해야합니다.
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하는 유저가없어요"));
        log.info("loadUserByUsername, user=[{}]", user.getEmail());
        return new PrincipalDetails(user);
    }

}