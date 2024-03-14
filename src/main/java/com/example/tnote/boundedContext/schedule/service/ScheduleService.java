package com.example.tnote.boundedContext.schedule.service;

import com.example.tnote.base.exception.schedule.ScheduleErrorResult;
import com.example.tnote.base.exception.schedule.ScheduleException;
import com.example.tnote.base.exception.user.UserErrorResult;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.boundedContext.schedule.dto.ScheduleDeleteResponseDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleRequestDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleResponseDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleUpdateRequestDto;
import com.example.tnote.boundedContext.schedule.dto.SemesterNameResponseDto;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
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

    @Transactional
    public ScheduleResponseDto addSchedule(ScheduleRequestDto dto, Long userId) {

        User currentUser = userRepository.findById(userId).orElseThrow(
                () -> new UserException(UserErrorResult.USER_NOT_FOUND));

        Schedule schedule = dto.toEntity(currentUser);

        return ScheduleResponseDto.of(scheduleRepository.save(schedule));
    }

    @Transactional
    public ScheduleResponseDto updateSchedule(ScheduleUpdateRequestDto dto, Long scheduleId, Long userId) {

        User currentUser = checkCurrentUser(userId);
        Schedule schedule = authorizationWriter(scheduleId, currentUser);

        updateEachScheduleItem(dto, schedule);

        return ScheduleResponseDto.of(schedule);
    }

    private void updateEachScheduleItem(ScheduleUpdateRequestDto dto, Schedule schedule) {
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
    public ScheduleDeleteResponseDto deleteSchedule(Long scheduleId, Long userId) {

        User currentUser = checkCurrentUser(userId);
        Schedule own = authorizationWriter(scheduleId, currentUser);

        scheduleRepository.deleteById(own.getId());
        return ScheduleDeleteResponseDto.builder()
                .id(own.getId())
                .build();
    }

    private Schedule authorizationWriter(Long id, User member) {

        Schedule schedule = getSchedule(id);

        matchUserWithSchedule(schedule.getId(), member.getId());

        return schedule;

    }


    // 학기당 남은 일수
    @Transactional(readOnly = true)
    public long countLeftDays(LocalDate date, Long scheduleId, Long userId) {

        Schedule schedule = getSchedule(scheduleId);

        compareScheduleWithUser(userId, schedule);

        log.info(" 날짜 차이 : {} 일", date.until(schedule.getEndDate(), ChronoUnit.DAYS));

        return date.until(schedule.getEndDate(), ChronoUnit.DAYS);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getAllSubjectsInfoBySchedule(Long scheduleId, Long userId) {

        matchUserWithSchedule(scheduleId, userId);

        return ScheduleResponseDto.of(scheduleQueryRepository.findAllById(scheduleId));
    }

    @Transactional(readOnly = true)
    public List<SemesterNameResponseDto> searchSemester(String semesterName, Long userId) {

        checkUser(userId);

        List<Schedule> schedules = scheduleQueryRepository.findAllBySemesterName(semesterName);

        return schedules.stream()
                .map(SemesterNameResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findSchedule(Long scheduleId, Long userId) {

        matchUserWithSchedule(scheduleId, userId);

        return ScheduleResponseDto.excludeLastDayOf(scheduleRepository.findAllById(scheduleId));
    }

    @Transactional(readOnly = true)
    public List<SemesterNameResponseDto> findScheduleList(Long userId) {
        User currentUser = checkCurrentUser(userId);

        return SemesterNameResponseDto.of(scheduleQueryRepository.findAllByUserId(currentUser.getId()));
    }

    @Transactional
    public long countLeftClasses(LocalDate startDate, LocalDate endDate, Long userId, Long scheduleId) {

        int totalCnt = 0;
        HashMap<String, Integer> map = new HashMap<>();
        map.put("MONDAY", 0);
        map.put("TUESDAY", 0);
        map.put("WEDNESDAY", 0);
        map.put("THURSDAY", 0);
        map.put("FRIDAY", 0);
        map.put("SATURDAY", 0);
        map.put("SUNDAY", 0);

        for (LocalDate currentDate = startDate; !currentDate.isAfter(endDate);
             currentDate = currentDate.plusDays(1)) {
            String dayOfWeek = String.valueOf(currentDate.getDayOfWeek());

            map.put(dayOfWeek, map.get(dayOfWeek) + 1);
        }

        Schedule schedule = getSchedule(scheduleId);

        compareScheduleWithUser(userId, schedule);

        for (Subjects s : schedule.getSubjectsList()) {

            if (map.containsKey(String.valueOf(s.getClassDay()))) {
                totalCnt += map.get(String.valueOf(s.getClassDay()));
            }
        }

        return totalCnt;
    }

    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(
                () -> new ScheduleException(ScheduleErrorResult.SCHEDULE_NOT_FOUND));
    }

    private User checkCurrentUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserException(UserErrorResult.USER_NOT_FOUND));
    }

    private void matchUserWithSchedule(Long scheduleId, Long userId) {
        User currentUser = checkCurrentUser(userId);
        Schedule schedule = getSchedule(scheduleId);

        if (!schedule.getUser().equals(currentUser)) {
            log.warn("스케쥴 작성자와 현재 유저가 다른 유저입니다.");
            throw new UserException(UserErrorResult.WRONG_USRE);
        }
    }

    private void checkUser(Long userId) {
        if (userId == null) {
            log.warn("없는 user 입니다");
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }
    }

    private void compareScheduleWithUser(Long userId, Schedule schedule) {
        if (!schedule.getUser().getId().equals(userId)) {
            throw new UserException(UserErrorResult.WRONG_USRE);
        }
    }

}
