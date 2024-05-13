package com.example.tnote.boundedContext.user.service;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.utils.CookieUtils;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
import com.example.tnote.boundedContext.user.dto.UserAlarmUpdate;
import com.example.tnote.boundedContext.user.dto.UserMailResponse;
import com.example.tnote.boundedContext.user.dto.UserResponse;
import com.example.tnote.boundedContext.user.dto.UserUpdateRequest;
import com.example.tnote.boundedContext.user.entity.User;
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
                .orElseThrow(() -> CustomException.USER_NOT_FOUND);

        return UserResponse.of(user);
    }

    @Transactional
    public UserResponse updateAlarmInfo(Long userId, UserAlarmUpdate dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CustomException.USER_NOT_FOUND);

        user.updateAlarm(dto.isAlarm());

        return UserResponse.of(user);
    }

    @Transactional
    public UserResponse updateExtraInfo(Long userId, UserUpdateRequest dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> CustomException.USER_NOT_FOUND);

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
            user.updateLastScheduleName(dto.getLastScheduleName());
        }
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> CustomException.USER_NOT_FOUND);

        // TODO : 로그아웃 진행시 리프래쉬 삭제 ( refresh 토큰 블랙 리스팅 )
        CookieUtils.deleteCookie(request, response, "AccessToken");
    }

    @Transactional(readOnly = true)
    public UserMailResponse getMail(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> CustomException.USER_NOT_FOUND);
        return UserMailResponse.of(currentUser);
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> CustomException.USER_NOT_FOUND);
        if (user.getSchool() == null || user.getSchool().isEmpty()) {
            throw CustomException.USER_NOT_FOUND;
        }
        return UserResponse.of(user);
    }
}
