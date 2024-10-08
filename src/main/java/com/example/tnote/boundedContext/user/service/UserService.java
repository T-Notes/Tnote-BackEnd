package com.example.tnote.boundedContext.user.service;


import static com.example.tnote.boundedContext.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.example.tnote.base.utils.CookieUtils;
import com.example.tnote.boundedContext.consultation.repository.ConsultationRepository;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleQueryRepository;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.dto.UserAlarmUpdate;
import com.example.tnote.boundedContext.user.dto.UserMailResponse;
import com.example.tnote.boundedContext.user.dto.UserResponse;
import com.example.tnote.boundedContext.user.dto.UserUpdateRequest;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.exception.UserException;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
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
    private final ScheduleRepository scheduleRepository;
    protected final ConsultationRepository consultationRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;

    @Transactional
    public UserResponse signUp(final String email, final String name) {

        User user = User.builder()
                .email(email)
                .username(name)
                .build();

        return UserResponse.from(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserInfo(final Long userId) {
        return UserResponse.from(userRepository.findUserById(userId));
    }

    @Transactional
    public UserResponse updateAlarmInfo(final Long userId, final UserAlarmUpdate dto) {
        User user = userRepository.findUserById(userId);

        user.updateAlarm(dto.isAlarm());

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateExtraInfo(final Long userId, final UserUpdateRequest dto) {

        User user = userRepository.findUserById(userId);
        List<Schedule> scheduleList = scheduleQueryRepository.findAllByUserId(userId);

        if (scheduleList.isEmpty()) {
            LocalDate currentDate = LocalDate.now();
            int date = currentDate.getMonthValue() <= 6 ? 1 : 2;

            Schedule schedule = getBuild(currentDate, date, user);

            scheduleRepository.save(schedule);

            user.updateLastScheduleId(schedule.getId());
            user.updateLastScheduleName(schedule.getSemesterName());
        }

        updateUserItem(dto, user);

        return UserResponse.from(user);
    }

    private Schedule getBuild(LocalDate currentDate, int date, User user) {
        return Schedule.builder()
                .semesterName(currentDate.getYear() + "년 " + date + "학기")
                .lastClass("9교시")
                .startDate(getStartDate(currentDate.getMonthValue(), currentDate))
                .endDate(getEndDate(currentDate.getMonthValue(), currentDate))
                .user(user)
                .build();
    }

    private LocalDate getStartDate(int currentMonth, LocalDate currentDate) {
        return currentMonth <= 6
                ? LocalDate.of(currentDate.getYear(), Month.JANUARY, 1)
                : LocalDate.of(currentDate.getYear(), Month.JULY, 1);
    }

    private LocalDate getEndDate(int currentMonth, LocalDate currentDate) {
        return currentMonth <= 6
                ? LocalDate.of(currentDate.getYear(), Month.JUNE, 30)
                : LocalDate.of(currentDate.getYear(), Month.DECEMBER, 31);
    }

    private void updateUserItem(final UserUpdateRequest dto, final User user) {
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
    public void logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, "AccessToken");
    }

    @Transactional(readOnly = true)
    public UserMailResponse getMail(final Long userId) {
        return UserMailResponse.from(userRepository.findUserById(userId));
    }

    @Transactional(readOnly = true)
    public UserResponse findById(final Long userId) {
        User user = userRepository.findUserById(userId);
        if (user.getSchool() == null || user.getSchool().isEmpty()) {
            throw new UserException(USER_NOT_FOUND);
        }
        return UserResponse.from(user);
    }
}
