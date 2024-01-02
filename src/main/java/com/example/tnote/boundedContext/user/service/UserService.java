package com.example.tnote.boundedContext.user.service;

import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.base.utils.CookieUtils;
import com.example.tnote.boundedContext.RefreshToken.repository.RefreshTokenRepository;
import com.example.tnote.boundedContext.user.dto.UserRequest;
import com.example.tnote.boundedContext.user.dto.UserResponse;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

@Transactional(readOnly = true)
@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    @Transactional
    public UserResponse signUp(String email, String name) {

        User user = User.builder()
                .email(email)
                .username(name)
                .build();

        return UserResponse.of(userRepository.save(user));
    }

    @Transactional
    public UserResponse updateExtraInfo(Long userId, UserRequest request) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));


        log.info("과목, 학교, 경력, 알람 수신 여부 등록 및 수정");
        if(StringUtils.hasText(request.getSubject())) {
            user.updateSubject(request.getSubject());
        }
        if(StringUtils.hasText(request.getSchool())) {
            user.updateSchool(request.getSchool());
        }
        if(Objects.nonNull(request.getCareer())) {
            user.updateCareer(request.getCareer());
        }
        if(Objects.nonNull(request.isAlarm()) && request.isAlarm()) {
            user.updateAlarm(true);
        }

        return UserResponse.of(user);
    }

    @Transactional
    public void deleteUser(User user) {
        if (user.getEmail().isEmpty()) {
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }

        log.info("refresh token, user entity 삭제");
        refreshTokenRepository.deleteByKeyEmail(user.getEmail());
        userRepository.delete(user);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response, User user) {

        if (user.getEmail().isEmpty()) {
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }

        CookieUtils.deleteCookie(request, response, "AccessToken");
    }
}
