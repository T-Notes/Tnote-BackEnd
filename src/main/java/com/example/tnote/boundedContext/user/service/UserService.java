package com.example.tnote.boundedContext.user.service;

import static com.example.tnote.boundedContext.user.exception.UserErrorCode.INVALID_REFRESH_TOKEN;
import static com.example.tnote.boundedContext.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.example.tnote.base.utils.CookieUtils;
import com.example.tnote.boundedContext.RefreshToken.entity.RefreshToken;
import com.example.tnote.boundedContext.RefreshToken.repository.RefreshTokenRepository;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
import com.example.tnote.boundedContext.user.dto.UserAlarmUpdate;
import com.example.tnote.boundedContext.user.dto.UserMailResponse;
import com.example.tnote.boundedContext.user.dto.UserResponse;
import com.example.tnote.boundedContext.user.dto.UserUpdateRequest;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.exception.UserException;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    protected final ConsultationRepository consultationRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public UserResponse signUp(String email, String name) {

        User user = User.builder()
                .email(email)
                .username(name)
                .build();

        return UserResponse.of(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserInfo(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        return UserResponse.of(user);
    }

    @Transactional
    public UserResponse updateAlarmInfo(Long userId, UserAlarmUpdate dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        user.updateAlarm(dto.isAlarm());

        return UserResponse.of(user);
    }

    @Transactional
    public UserResponse updateExtraInfo(Long userId, UserUpdateRequest dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        updateUserItem(dto, user);

        return UserResponse.of(user);
    }

    private void updateUserItem(UserUpdateRequest dto, User user) {
        if (dto.hasSchoolName()) {
            user.updateSchool(dto.getSchoolName());
        }
        if (dto.hasSubject()) {
            user.updateSubject(dto.getSubject());
        }
        if (dto.hasCareer()) {
            user.updateCareer(dto.getCareer());
        }
        if (dto.hasSchedule()) {
            user.updateLastScheduleId(dto.getScheduleId());
        }
        if (dto.hasScheduleName()) {
            user.updateLastScheduleName(dto.getSemesterName());
        }
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));

        RefreshToken refreshToken = refreshTokenRepository.findByKeyEmail(user.getEmail())
                .orElseThrow(() -> new UserException(INVALID_REFRESH_TOKEN));

        refreshTokenRepository.delete(refreshToken);

        CookieUtils.deleteCookie(request, response, "AccessToken");
    }

    @Transactional(readOnly = true)
    public UserMailResponse getMail(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(USER_NOT_FOUND));
        return UserMailResponse.of(currentUser);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserException(USER_NOT_FOUND));
        if (user.getSchool() == null || user.getSchool().isEmpty()) {
            throw new UserException(USER_NOT_FOUND);
        }
        return UserResponse.of(user);
    }
}
