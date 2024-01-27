package com.example.tnote.boundedContext.schedule.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.schedule.dto.ScheduleDeleteResponseDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleRequestDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleResponseDto;
import com.example.tnote.boundedContext.schedule.dto.ScheduleUpdateRequestDto;
import com.example.tnote.boundedContext.schedule.service.ScheduleService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tnote/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping
    public ResponseEntity<Result> saveSchedule(@RequestBody ScheduleRequestDto dto,
                                               @AuthenticationPrincipal PrincipalDetails user) {

        log.info("Schedule controller principal user : {}", user);
        ScheduleResponseDto response = scheduleService.addSchedule(dto, user.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    // 하나의 학기에 대한 정보 전달 ( 학기명, 기간, 마지막 교시 등 )
    @GetMapping("/{scheduleId}")
    public ResponseEntity<Result> findSchedule(@PathVariable Long scheduleId,
                                               @AuthenticationPrincipal PrincipalDetails user) {

        List<ScheduleResponseDto> response = scheduleService.findSchedule(scheduleId, user);

        return ResponseEntity.ok(Result.of(response));
    }

    // 학기 리스트 조회
    @GetMapping("/list")
    public ResponseEntity<Result> findScheduleList(@AuthenticationPrincipal PrincipalDetails user) {

        List<String> response = scheduleService.findScheduleList(user);

        return ResponseEntity.ok(Result.of(response));
    }

    @PatchMapping("/{scheduleId}")
    public ResponseEntity<Result> updateSchedule(@RequestBody ScheduleUpdateRequestDto dto,
                                                 @PathVariable("scheduleId") Long scheduleId,
                                                 @AuthenticationPrincipal PrincipalDetails user) {

        ScheduleResponseDto response = scheduleService.updateSchedule(dto, scheduleId, user.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Result> deleteSchedule(@PathVariable("scheduleId") Long scheduleId,
                                                 @AuthenticationPrincipal PrincipalDetails user) {

        ScheduleDeleteResponseDto response = scheduleService.deleteSchedule(scheduleId, user);

        return ResponseEntity.ok(Result.of(response));
    }

    // 남은 수업 일수 체크
    @GetMapping("/leftClassDays")
    public ResponseEntity<Result> countLeftDays(
            @RequestParam(defaultValue = "1970-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(defaultValue = "1970-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        long response = scheduleService.countLeftDays(startDate, endDate);

        return ResponseEntity.ok(Result.of(response));
    }

    // 남은 수업 횟수 체크
    @GetMapping("/leftClasses/{scheduleId}")
    public ResponseEntity<Result> countLeftClasses(
            @RequestParam(defaultValue = "1970-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(defaultValue = "1970-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @PathVariable("scheduleId") Long scheduleId) {
        long response = scheduleService.countLeftClasses(startDate, endDate, scheduleId);

        return ResponseEntity.ok(Result.of(response));
    }

    // 월~금 시간표에 넣을 데이터 조회
    @GetMapping("/week/{scheduleId}")
    public ResponseEntity<Result> findWeek(@PathVariable("scheduleId") Long scheduleId,
                                           @AuthenticationPrincipal PrincipalDetails user) {

        List<ScheduleResponseDto> response = scheduleService.getAll(scheduleId, user);
        return ResponseEntity.ok(Result.of(response));
    }

}
