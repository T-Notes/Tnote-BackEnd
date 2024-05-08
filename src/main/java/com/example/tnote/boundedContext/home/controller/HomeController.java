package com.example.tnote.boundedContext.home.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.base.utils.TokenUtils;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.home.constant.LogType;
import com.example.tnote.boundedContext.home.dto.ArchiveResponseDto;
import com.example.tnote.boundedContext.home.dto.ArchiveSliceResponseDto;
import com.example.tnote.boundedContext.home.service.HomeService;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.recentLog.dto.RecentLogResponseDto;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.dto.SemesterNameResponseDto;
import com.example.tnote.boundedContext.schedule.service.ScheduleService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tnote/home")
public class HomeController {

    private final HomeService homeService;
    private final ScheduleService scheduleService;
    private final RecentLogService recentLogService;

    // 학생 이름 검색 했을때 나올 내용 - keyword로 통합
    @GetMapping("/searching")
    public ResponseEntity<Result> findAll(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        List<ConsultationResponseDto> consultation = homeService.findAllOfConsultation(keyword, currentUser.getId());
        List<ObservationResponseDto> observation = homeService.findAllOfObservation(keyword, currentUser.getId());
        List<ClassLogResponseDto> classLog = homeService.findAllOfClassLog(keyword, currentUser.getId());
        List<ProceedingResponseDto> proceeding = homeService.findAllOfProceeding(keyword, currentUser.getId());

        List<Object> response = new ArrayList<>();
        response.addAll(consultation);
        response.addAll(observation);
        response.addAll(classLog);
        response.addAll(proceeding);

        return ResponseEntity.ok(Result.of(response));
    }

//    @GetMapping("/last/{scheduleId}")
//    public ResponseEntity<Result> getLastSchedule(@AuthenticationPrincipal PrincipalDetails user,
//                                                  @PathVariable Long scheduleId) {
//        LastScheduleResponseDto response = homeService.getLastSchedule(user.getId(), scheduleId);
//
//        return ResponseEntity.ok(Result.of(response));
//    }
//
//    @PostMapping("/last/{scheduleId}")
//    public ResponseEntity<Result> saveLastSchedule(@AuthenticationPrincipal PrincipalDetails user,
//                                                   @PathVariable Long scheduleId) {
//        LastScheduleResponseDto response = homeService.saveLastSchedule(user.getId(), scheduleId);
//
//        return ResponseEntity.ok(Result.of(response));
//    }

    // 아카이브 명 검색 ( = 학기명 검색 )
    @GetMapping("/semester")
    public ResponseEntity<Result> findSemester(
            @RequestParam(name = "semesterName", required = false, defaultValue = "") String semesterName,
            @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);
        List<SemesterNameResponseDto> response = scheduleService.searchSemester(semesterName, currentUser.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/{scheduleId}/dateLogs")
    public ResponseEntity<Result> readDateLogs(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                               @PathVariable Long scheduleId,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                               @RequestParam(value = "size", required = false, defaultValue = "8") int size,
                                               @RequestParam(value = "logType", required = false, defaultValue = "CLASS_LOG") LogType logType) {

        PageRequest pageRequest = PageRequest.of(page, size);
        ArchiveSliceResponseDto response = homeService.readLogsByDate(principalDetails.getId(), scheduleId, startDate,
                endDate, logType, pageRequest);
        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/{scheduleId}/dailyLogs")
    public ResponseEntity<Result> readDailyLogs(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @PathVariable Long scheduleId,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        ArchiveResponseDto response = homeService.readDailyLogs(principalDetails.getId(), scheduleId, date);
        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/{scheduleId}/monthlyLogs")
    public ResponseEntity<Result> readMonthlyLogs(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                  @PathVariable Long scheduleId,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        ArchiveResponseDto response = homeService.readMonthlyLogs(principalDetails.getId(), scheduleId, date);
        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/recentLogs")
    public ResponseEntity<Result> getRecentClassLogs(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.of("Unauthorized"));
        }

        List<RecentLogResponseDto> response = recentLogService.getRecentLogsFromDatabase(principalDetails.getId());

        return ResponseEntity.ok(Result.of(response));
    }
}
