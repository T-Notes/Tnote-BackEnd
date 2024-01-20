package com.example.tnote.boundedContext.home.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.home.dto.ArchiveResponseDto;
import com.example.tnote.boundedContext.home.service.HomeService;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
    public ResponseEntity<Result> findAll(@RequestParam(name = "studentName", defaultValue = "") String studentName,
                                          @AuthenticationPrincipal PrincipalDetails user) {

        List<ConsultationResponseDto> consultation = homeService.findAllOfConsultation(studentName, user);
        List<ObservationResponseDto> observation = homeService.findAllOfObservation(studentName, user);
        //자바 17버전에서 는 toList()만으로도 바꿀수 있어요! 한번 사용해 보세욥 ㅎㅎ
        //그리고 List<String> 보다는 객체를 하나 만들어서 넘겨주는게 더 좋아보여요 굳이 저렇게 형변환을 하는것 보다 더 일이 적을것 같구요 어떻게 생각하시나요??
        List<String> response = (List<String>) Stream.of(consultation, observation)
                .flatMap(x -> x.stream())
                .collect(Collectors.toList());
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
