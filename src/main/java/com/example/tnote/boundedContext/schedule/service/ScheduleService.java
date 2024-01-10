package com.example.tnote.boundedContext.schedule.service;

import com.example.tnote.base.exception.*;
import com.example.tnote.boundedContext.schedule.dto.ScheduleRequestDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleResponseDto;
import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleQueryRepository;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    @Transactional
    public ScheduleResponseDto addSchedule(ScheduleRequestDto dto, PrincipalDetails user) {

        Optional<User> currentUser = userRepository.findById(user.getId());
        if(currentUser.isEmpty()) {
            log.warn("user is empty : {}", currentUser);
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }

        Schedule schedule = Schedule.builder()
                .lastDay(dto.getLastDay()) // 삭제 예정
                .semesterName(dto.getSemesterName())
                .lastClass(dto.getLastClass())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .user(currentUser.get())
                .build();

        Schedule saved = scheduleRepository.save(schedule);

        return ScheduleResponseDto.of(saved);
    }

    public ScheduleResponseDto updateSchedule(ScheduleRequestDto dto, Long scheduleId, PrincipalDetails user) {

        User currentUser = checkCurrentUser(user.getId());
        Schedule schedule = authorizationWriter(scheduleId, currentUser);

        if(dto.getSemesterName() != null) {
            schedule.updateSemesterName(dto.getSemesterName());
        }

        // 삭제 예정
        if(dto.getLastDay() != null) {
            schedule.updateLastDay(dto.getLastDay());
        }

        if(dto.getLastClass() != null) {
            schedule.updateLastClass(dto.getLastClass());
        }
        if(dto.getStartDate() != null) {
            schedule.updateStartDate(dto.getStartDate());
        }
        if(dto.getEndDate() != null) {
            schedule.updateEndDate(dto.getEndDate());
        }

        return ScheduleResponseDto.of(schedule);
    }

    public String deleteSchedule(Long scheduleId, PrincipalDetails user) {

        User currentUser = checkCurrentUser(user.getId());
        Schedule own = authorizationWriter(scheduleId, currentUser);

        scheduleRepository.deleteById(own.getId());
        return "학기가 삭제되었습니다.";
    }

    @Transactional
    public Schedule authorizationWriter(Long id, User member) {

        Schedule schedule = scheduleRepository.findById(id).orElseThrow(
                () -> new ScheduleException(ScheduleErrorResult.SCHEDULE_NOT_FOUND));

        if (!schedule.getUser().getId().equals(member.getId())) {
            log.warn("member doesn't have authentication , user {}", schedule.getUser());
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }
        return schedule;

    }

    public User checkCurrentUser(Long id) {
        Optional<User> currentUser = userRepository.findById(id);

        if (currentUser.isEmpty()) {
            log.warn("해당하는 유저가 없습니다. currentUser : {}", currentUser);
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }

        return currentUser.get();
    }


    // 학기당 남은 일수
    @Transactional
    public long countLeftDays(LocalDate startDate, LocalDate endDate) {

        log.info(" 날짜 차이 : {} 일",startDate.until(endDate, ChronoUnit.DAYS));

        return startDate.until(endDate, ChronoUnit.DAYS);
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getAll(Long scheduleId, PrincipalDetails user ) {

        User currentUser = checkCurrentUser(user.getId());
        Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);

        if(schedule.isEmpty()) {
            log.warn("없는 학기입니다.");
            throw new ScheduleException(ScheduleErrorResult.SCHEDULE_NOT_FOUND);
        }

        if(!schedule.get().getUser().equals(currentUser)) {
            log.warn("스케쥴 작성자와 현재 유저가 다른 유저입니다.");
            throw new UserException(UserErrorResult.WRONG_USRE);
        }

        return ScheduleResponseDto.of(scheduleRepository.findAllById(scheduleId));
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> findAll(PrincipalDetails user) {
        if(user == null) {
            log.warn("없는 user 입니다");
            throw new UserException(UserErrorResult.USER_NOT_FOUND);
        }

        return ScheduleResponseDto.excludeLastDayAndLastClassOf(scheduleRepository.findAll());
    }
}
