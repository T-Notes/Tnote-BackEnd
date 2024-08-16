package com.example.tnote.boundedContext.schedule.service;


import static com.example.tnote.boundedContext.schedule.exception.ScheduleErrorCode.SCHEDULE_NOT_FOUND;
import static com.example.tnote.boundedContext.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.example.tnote.boundedContext.classLog.repository.query.ClassLogQueryRepository;
import com.example.tnote.boundedContext.consultation.repository.query.ConsultationQueryRepository;
import com.example.tnote.boundedContext.observation.repository.query.ObservationQueryRepository;
import com.example.tnote.boundedContext.proceeding.repository.query.ProceedingQueryRepository;
import com.example.tnote.boundedContext.recentLog.repository.RecentLogRepository;
import com.example.tnote.boundedContext.schedule.dto.ScheduleDeleteResponseDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleRequestDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleResponseDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleUpdateRequestDto;
import com.example.tnote.boundedContext.schedule.dto.SemesterNameResponseDto;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.exception.ScheduleException;
import com.example.tnote.boundedContext.schedule.repository.ScheduleQueryRepository;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.subject.entity.Subjects;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ScheduleQueryRepository scheduleQueryRepository;
    private final ClassLogQueryRepository classLogQueryRepository;
    private final ProceedingQueryRepository proceedingQueryRepository;
    private final ConsultationQueryRepository consultationQueryRepository;
    private final ObservationQueryRepository observationQueryRepository;
    private final RecentLogRepository recentLogRepository;

    @Transactional
    public ScheduleResponseDto addSchedule(final ScheduleRequestDto dto, final Long userId) {

        User currentUser = userRepository.findById(userId).orElseThrow(
                () -> new ScheduleException(USER_NOT_FOUND));

        Schedule schedule = dto.toEntity(currentUser);

        return ScheduleResponseDto.from(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(final ScheduleUpdateRequestDto dto, final Long scheduleId,
                                              final Long userId) {

        User currentUser = checkCurrentUser(userId);
        Schedule schedule = authorizationWriter(scheduleId, currentUser);

        updateEachScheduleItem(dto, schedule);

        return ScheduleResponseDto.from(schedule);
    }

    private void updateEachScheduleItem(final ScheduleUpdateRequestDto dto, final Schedule schedule) {
        if (dto.hasSemesterName()) {
            schedule.updateSemesterName(dto.getSemesterName());
        }
        if (dto.hasLastClass()) {
            schedule.updateLastClass(dto.getLastClass());
        }
        if (dto.hasStartDate()) {
            schedule.updateStartDate(dto.getStartDate());
        }
        if (dto.hasEndDate()) {
            schedule.updateEndDate(dto.getEndDate());
        }
    }

    @Transactional
    public ScheduleDeleteResponseDto deleteSchedule(final Long scheduleId, final Long userId) {

        User currentUser = checkCurrentUser(userId);
        Schedule own = authorizationWriter(scheduleId, currentUser);

        scheduleRepository.deleteById(own.getId());

        classLogQueryRepository.deleteAllByScheduleIdAndUserId(scheduleId, userId);
        proceedingQueryRepository.deleteAllByScheduleIdAndUserId(scheduleId, userId);
        consultationQueryRepository.deleteAllByScheduleIdAndUserId(scheduleId, userId);
        observationQueryRepository.deleteAllByScheduleIdAndUserId(scheduleId, userId);
        recentLogRepository.deleteAllByUserIdAndScheduleId(userId, scheduleId);

        if (currentUser.getLastScheduleId() == scheduleId) {
            currentUser.updateLastScheduleName(null);
            currentUser.updateLastScheduleId(0);
        }

        return ScheduleDeleteResponseDto.from(own);
    }

    private Schedule authorizationWriter(final Long id, final User member) {

        Schedule schedule = getSchedule(id);

        matchUserWithSchedule(schedule.getId(), member.getId());

        return schedule;

    }


    // 학기당 남은 일수
    @Transactional(readOnly = true)
    public long countLeftDays(final LocalDate date, final Long scheduleId, final Long userId) {

        Schedule schedule = getSchedule(scheduleId);

        LocalDate CurrentDate = getLocalDate(date);

        compareScheduleWithUser(userId, schedule);

        return ChronoUnit.DAYS.between(CurrentDate, schedule.getEndDate()) <= 0 ? 0
                : ChronoUnit.DAYS.between(CurrentDate, schedule.getEndDate());

    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getAllSubjectsInfoBySchedule(final Long scheduleId, final Long userId) {

        matchUserWithSchedule(scheduleId, userId);

        return ScheduleResponseDto.from(scheduleQueryRepository.findAllById(scheduleId));
    }

    @Transactional(readOnly = true)
    public List<SemesterNameResponseDto> searchSemester(final String semesterName, final Long userId) {

        checkUser(userId);

        List<Schedule> schedules = scheduleQueryRepository.findAllBySemesterNameAndUserId(semesterName, userId);

        return schedules.stream()
                .map(SemesterNameResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findSchedule(final Long scheduleId, final Long userId) {

        matchUserWithSchedule(scheduleId, userId);

        return ScheduleResponseDto.excludeLastDayFrom(scheduleRepository.findAllById(scheduleId));
    }

    @Transactional(readOnly = true)
    public List<SemesterNameResponseDto> findScheduleList(final Long userId) {
        User currentUser = checkCurrentUser(userId);

        return SemesterNameResponseDto.from(scheduleQueryRepository.findAllByUserId(currentUser.getId()));
    }

    @Transactional
    public long countLeftClasses(final LocalDate date, final Long userId, final Long scheduleId) {

        int totalCnt = 0;
        HashMap<String, Integer> map = new HashMap<>();
        map.put("MONDAY", 0);
        map.put("TUESDAY", 0);
        map.put("WEDNESDAY", 0);
        map.put("THURSDAY", 0);
        map.put("FRIDAY", 0);
        map.put("SATURDAY", 0);
        map.put("SUNDAY", 0);

        Schedule schedule = getSchedule(scheduleId);

        for (LocalDate currentDate = date; !currentDate.isAfter(schedule.getEndDate());
             currentDate = currentDate.plusDays(1)) {
            String dayOfWeek = String.valueOf(currentDate.getDayOfWeek());

            map.put(dayOfWeek, map.get(dayOfWeek) + 1);
        }

        compareScheduleWithUser(userId, schedule);

        for (Subjects s : schedule.getSubjectsList()) {

            if (map.containsKey(String.valueOf(s.getClassDay()))) {
                totalCnt += map.get(String.valueOf(s.getClassDay()));
            }
        }

        return totalCnt;
    }

    private Schedule getSchedule(final Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ScheduleException(SCHEDULE_NOT_FOUND));
    }

    private User checkCurrentUser(final Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new ScheduleException(USER_NOT_FOUND));
    }

    private void matchUserWithSchedule(final Long scheduleId, final Long userId) {
        User currentUser = checkCurrentUser(userId);
        Schedule schedule = getSchedule(scheduleId);

        if (!schedule.getUser().equals(currentUser)) {
            log.warn("스케쥴 작성자와 현재 유저가 다른 유저입니다.");
            throw new ScheduleException(USER_NOT_FOUND);
        }
    }

    private void checkUser(final Long userId) {
        if (userId == null) {
            log.warn("없는 user 입니다");
            throw new ScheduleException(USER_NOT_FOUND);
        }
    }

    private void compareScheduleWithUser(final Long userId, final Schedule schedule) {
        if (!schedule.getUser().getId().equals(userId)) {
            throw new ScheduleException(USER_NOT_FOUND);
        }
    }

    private LocalDate getLocalDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return date;
    }

}
