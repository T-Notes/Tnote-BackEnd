package com.example.tnote.boundedContext.schedule.service;


import com.example.tnote.base.exception.CustomException;
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
import com.example.tnote.boundedContext.user.service.UserService;
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
    private final UserService userService;

    @Transactional
    public ScheduleResponseDto addSchedule(ScheduleRequestDto dto, Long userId) {

        User currentUser = userRepository.findById(userId).orElseThrow(
                () -> CustomException.USER_NOT_FOUND);

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

        currentUser.updateLastScheduleName(null);
        currentUser.updateLastScheduleId(0);

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

        LocalDate CurrentDate = getLocalDate(date);

        compareScheduleWithUser(userId, schedule);

        return ChronoUnit.DAYS.between(CurrentDate, schedule.getEndDate()) <= 0 ? 0
                : ChronoUnit.DAYS.between(CurrentDate, schedule.getEndDate());

    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getAllSubjectsInfoBySchedule(Long scheduleId, Long userId) {

        matchUserWithSchedule(scheduleId, userId);

        return ScheduleResponseDto.of(scheduleQueryRepository.findAllById(scheduleId));
    }

    @Transactional(readOnly = true)
    public List<SemesterNameResponseDto> searchSemester(String semesterName, Long userId) {

        checkUser(userId);

        List<Schedule> schedules = scheduleQueryRepository.findAllBySemesterNameAndUserId(semesterName, userId);

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
    public long countLeftClasses(LocalDate date, Long userId, Long scheduleId) {

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

    private Schedule getSchedule(Long scheduleId) {
        return scheduleRepository.findById(scheduleId).orElseThrow(
                () -> CustomException.SCHEDULE_NOT_FOUND);
    }

    private User checkCurrentUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> CustomException.USER_NOT_FOUND);
    }

    private void matchUserWithSchedule(Long scheduleId, Long userId) {
        User currentUser = checkCurrentUser(userId);
        Schedule schedule = getSchedule(scheduleId);

        if (!schedule.getUser().equals(currentUser)) {
            log.warn("스케쥴 작성자와 현재 유저가 다른 유저입니다.");
            throw CustomException.USER_NOT_FOUND;
        }
    }

    private void checkUser(Long userId) {
        if (userId == null) {
            log.warn("없는 user 입니다");
            throw CustomException.USER_NOT_FOUND;
        }
    }

    private void compareScheduleWithUser(Long userId, Schedule schedule) {
        if (!schedule.getUser().getId().equals(userId)) {
            throw CustomException.USER_NOT_FOUND;
        }
    }

    private LocalDate getLocalDate(LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return date;
    }

}
