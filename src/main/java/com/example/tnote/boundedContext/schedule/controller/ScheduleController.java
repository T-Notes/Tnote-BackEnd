package com.example.tnote.boundedContext.schedule.controller;

import com.example.tnote.base.exception.ScheduleErrorResult;
import com.example.tnote.base.exception.ScheduleException;
import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.schedule.dto.ScheduleRequestDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleResponseDto;
import com.example.tnote.boundedContext.schedule.dto.SubjectRequestDto;
import com.example.tnote.boundedContext.schedule.dto.SubjectResponseDto;
import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.schedule.service.ScheduleService;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleRepository scheduleRepository;

    @PostMapping
    public ResponseEntity<Result> saveSemester(@RequestBody ScheduleRequestDto dto,
                                               @AuthenticationPrincipal PrincipalDetails user) {

        log.info("Schedule controller principal user : {}", user);
        ScheduleResponseDto response = scheduleService.addSchedule(dto, user);

        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping
    public ResponseEntity<Result> findAll(@AuthenticationPrincipal PrincipalDetails user) {

        List<ScheduleResponseDto> response = scheduleService.findAll(user);

        return ResponseEntity.ok(Result.of(response));

    }

    @PatchMapping("/{scheduleId}")
    public ResponseEntity<Result> updateSchedule(@RequestBody ScheduleRequestDto dto,
                                                 @PathVariable("scheduleId") Long scheduleId,
                                                 @AuthenticationPrincipal PrincipalDetails user) {

        ScheduleResponseDto response = scheduleService.updateSchedule(dto, scheduleId, user);

        return ResponseEntity.ok(Result.of(response));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Result> deleteSchedule(@PathVariable("scheduleId") Long scheduleId,
                                                 @AuthenticationPrincipal PrincipalDetails user) {

        String response = scheduleService.deleteSchedule(scheduleId, user);

        return ResponseEntity.ok(Result.of(response));
    }

    // 남은 수업 일수 체크
    @GetMapping("/leftClassDays")
    public ResponseEntity<Result> countLeftDays(@RequestBody ScheduleRequestDto dto) {
        long response = scheduleService.countLeftDays(dto.getStartDate(), dto.getEndDate());

        return ResponseEntity.ok(Result.of(response));
    }

    // 남은 수업 횟수 체크
    @GetMapping("/leftClasses/{scheduleId}")
    public ResponseEntity<Result> countLeftClasses(@RequestBody ScheduleRequestDto dto, @PathVariable("scheduleId") Long scheduleId) {
        long response = scheduleService.countLeftClasses(dto.getStartDate(), dto.getEndDate(), scheduleId);

        return ResponseEntity.ok(Result.of(response));
    }

    // 월~금 시간표에 넣을 데이터 조회
    @GetMapping("/week/{scheduleId}")
    public ResponseEntity<Result> findWeek(@PathVariable("scheduleId") Long scheduleId, @AuthenticationPrincipal PrincipalDetails user) {

        List<ScheduleResponseDto> response = scheduleService.getAll(scheduleId, user);
        return ResponseEntity.ok(Result.of(response));
    }

}
