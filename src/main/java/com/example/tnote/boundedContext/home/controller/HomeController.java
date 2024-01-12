package com.example.tnote.boundedContext.home.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.home.service.HomeService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        //List<ObservationResponseDto> observation = homeService.findAllOfObservation(studentName, user);

//        List<String> response = Stream.of(consultation, observation)
//                .flatMap(x -> x.stream())
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(Result.of(response));

        return ResponseEntity.ok(Result.of(consultation));

    }
}
