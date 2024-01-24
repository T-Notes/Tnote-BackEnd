package com.example.tnote.boundedContext.home.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.home.dto.ArchiveResponseDto;
import com.example.tnote.boundedContext.home.service.HomeService;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    // 학생 이름 검색 했을때 나올 내용
    @GetMapping("/searching")
    public ResponseEntity<Result> findAll(
            @RequestParam(name = "studentName", required = false, defaultValue = "") String studentName,
            @RequestParam(name = "title", required = false, defaultValue = "") String title,
            @AuthenticationPrincipal PrincipalDetails user) {

        List<ConsultationResponseDto> consultation = homeService.findAllOfConsultation(studentName, user);
        List<ObservationResponseDto> observation = homeService.findAllOfObservation(studentName, user);
        List<ClassLogResponseDto> classLog = homeService.findAllOfClassLog(title, user);
        List<ProceedingResponseDto> proceeding = homeService.findAllOfProceeding(title, user);

        List<Object> response = new ArrayList<>();
        response.addAll(consultation);
        response.addAll(observation);
        response.addAll(classLog);
        response.addAll(proceeding);

        return ResponseEntity.ok(Result.of(response));

    }

    @GetMapping("/dailyLogs")
    public ResponseEntity<Result> readDailyLogs(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        if (date == null) {
            date = LocalDate.now(); // 날짜가 제공되지 않으면 현재 날짜 사용
        }

        ArchiveResponseDto response = homeService.readDailyLogs(principalDetails.getId(), date);
        return ResponseEntity.ok(Result.of(response));
    }
}
