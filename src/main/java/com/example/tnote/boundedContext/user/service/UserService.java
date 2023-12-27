package com.example.tnote.boundedContext.user.service;

import com.example.tnote.boundedContext.user.dto.UserResponse;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    @Transactional
    public UserResponse signUp(String email, String name) {

        User user = User.builder()
                .email(email)
                .username(name)
                .build();

        return UserResponse.of(userRepository.save(user));
    }
}
