package com.example.tnote.boundedContext.home.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.home.dto.ArchiveResponseDto;
import com.example.tnote.boundedContext.home.service.HomeService;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
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

    // 학생 이름 검색 했을때 나올 내용
    @GetMapping("/searching")
    public ResponseEntity<Result> findAll(
            @RequestParam(name = "studentName", required = false, defaultValue = "") String studentName,
            @RequestParam(name = "title", required = false, defaultValue = "") String title,
            @AuthenticationPrincipal PrincipalDetails user) {

        List<ConsultationResponseDto> consultation = homeService.findAllOfConsultation(studentName, user.getId());
        List<ObservationResponseDto> observation = homeService.findAllOfObservation(studentName, user.getId());
        List<ClassLogResponseDto> classLog = homeService.findAllOfClassLog(title, user.getId());
        List<ProceedingResponseDto> proceeding = homeService.findAllOfProceeding(title, user.getId());

        List<Object> response = new ArrayList<>();
        response.addAll(consultation);
        response.addAll(observation);
        response.addAll(classLog);
        response.addAll(proceeding);

        return ResponseEntity.ok(Result.of(response));

    }

    // 아카이브 명 검색 ( = 학기명 검색 )
    @GetMapping("/semester")
    public ResponseEntity<Result> findSemester(
            @RequestParam(name = "semesterName", required = false, defaultValue = "") String semesterName,
            @AuthenticationPrincipal PrincipalDetails user) {

        List<SemesterNameResponseDto> response = scheduleService.searchSemester(semesterName, user.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/{scheduleId}/dailyLogs")
    public ResponseEntity<Result> readDailyLogs(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @PathVariable Long scheduleId,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                @RequestParam(value = "size", required = false, defaultValue = "8") int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        ArchiveResponseDto response = homeService.readDailyLogs(principalDetails.getId(), scheduleId, startDate, endDate, pageRequest);
        return ResponseEntity.ok(Result.of(response));
    }
}
