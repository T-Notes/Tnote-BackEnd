package com.example.tnote.boundedContext.user.service;

import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.base.utils.CookieUtils;
import com.example.tnote.boundedContext.RefreshToken.repository.RefreshTokenRepository;
import com.example.tnote.boundedContext.classLog.repository.ClassLogRepository;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
import com.example.tnote.boundedContext.observation.repository.ObservationRepository;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingRepository;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.todo.repository.TodoRepository;
import com.example.tnote.boundedContext.user.dto.UserDeleteResponseDto;
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
    private final ClassLogRepository classLogRepository;
    private final ProceedingRepository proceedingRepository;
    private final TodoRepository todoRepository;
    private final ObservationRepository observationRepository;
    protected final ConsultationRepository consultationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ScheduleRepository scheduleRepository;

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
                .orElseThrow(() -> UserException.USER_NOT_FOUND);

        return UserResponse.of(user);
    }

    @Transactional
    public UserResponse updateExtraInfo(Long userId, UserUpdateRequest dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.USER_NOT_FOUND);

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
        if (dto.hasAlarm()) {
            user.updateAlarm(dto.isAlarm());
        }
    }

    @Transactional
    public UserDeleteResponseDto deleteUser(Long userId) {

        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> UserException.USER_NOT_FOUND);

        deleteAll(userId, currentUser);

        return UserDeleteResponseDto.builder()
                .id(currentUser.getId())
                .build();
    }

    // 연관키로 묶여 있음
    private void deleteAll(Long userId, User currentUser) {
        classLogRepository.deleteAllByUserId(userId);
        proceedingRepository.deleteAllByUserId(userId);
        consultationRepository.deleteAllByUserId(userId);
        observationRepository.deleteAllByUserId(userId);
        todoRepository.deleteAllByUserId(userId);
        scheduleRepository.deleteAllByUserId(userId);
        refreshTokenRepository.deleteByKeyEmail(currentUser.getEmail());
        userRepository.delete(currentUser);
    }

    @Transactional
    public void logout(HttpServletRequest request, HttpServletResponse response, Long userId) {

        userRepository.findById(userId)
                .orElseThrow(() -> UserException.USER_NOT_FOUND);

        CookieUtils.deleteCookie(request, response, "AccessToken");
    }

    @Transactional(readOnly = true)
    public UserMailResponse getMail(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> UserException.USER_NOT_FOUND);
        return UserMailResponse.of(currentUser);
    }

}
