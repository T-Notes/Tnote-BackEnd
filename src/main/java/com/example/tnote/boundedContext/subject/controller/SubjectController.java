package com.example.tnote.boundedContext.subject.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.subject.dto.SubjectRequestDto;
import com.example.tnote.boundedContext.subject.dto.SubjectResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectsDeleteResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectsUpdateRequestDto;
import com.example.tnote.boundedContext.subject.service.SubjectService;
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
@RequestMapping("/tnote/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<Result> saveSubjects(@RequestBody SubjectRequestDto dto,
                                               @AuthenticationPrincipal PrincipalDetails user) {

        SubjectResponseDto response = subjectService.addSubjects(dto, user.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    @PatchMapping("/{subjectsId}")
    public ResponseEntity<Result> updateSubjects(@RequestBody SubjectsUpdateRequestDto dto,
                                                 @PathVariable("subjectsId") Long subjectsId,
                                                 @AuthenticationPrincipal PrincipalDetails user) {

        SubjectResponseDto response = subjectService.updateSubjects(dto, subjectsId, user.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    @DeleteMapping("/{scheduleId}/{subjectsId}")
    public ResponseEntity<Result> deleteSubjects(@PathVariable Long scheduleId,
                                                 @PathVariable Long subjectsId,
                                                 @AuthenticationPrincipal PrincipalDetails user) {

        SubjectsDeleteResponseDto response = subjectService.deleteSubjects(scheduleId, subjectsId, user.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    // 시간표에서 특정 요일에 대한 데이터 조회
    @GetMapping("/details/{scheduleId}/{day}")
    public ResponseEntity<Result> findDay(@PathVariable Long scheduleId,
                                          @PathVariable ClassDay day,
                                          @AuthenticationPrincipal PrincipalDetails user) {

        List<SubjectResponseDto> response = subjectService.getMyClass(scheduleId, day, user);
        return ResponseEntity.ok(Result.of(response));
    }

    // 홈페이지에서 오늘 데이터 조회
    @GetMapping("/details/{scheduleId}")
    public ResponseEntity<Result> findToday(@PathVariable Long scheduleId,
                                            @AuthenticationPrincipal PrincipalDetails user,
                                            @RequestParam(defaultValue = "1970-01-01") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate today) {
        log.info("today : {} --------------------------", today);
        List<SubjectResponseDto> response = subjectService.getTodayClass(scheduleId, user, today);
        return ResponseEntity.ok(Result.of(response));
    }
}
