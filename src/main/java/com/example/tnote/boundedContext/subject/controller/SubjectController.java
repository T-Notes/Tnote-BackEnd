package com.example.tnote.boundedContext.subject.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.base.utils.TokenUtils;
import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.subject.dto.SubjectDetailResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectRequestDto;
import com.example.tnote.boundedContext.subject.dto.SubjectResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectsDeleteResponseDto;
import com.example.tnote.boundedContext.subject.dto.SubjectsUpdateRequestDto;
import com.example.tnote.boundedContext.subject.service.SubjectService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/tnote/v1/subject")
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping("/{scheduleId}")
    public ResponseEntity<Result> saveSubjects(@RequestBody SubjectRequestDto dto,
                                               @PathVariable("scheduleId") Long scheduleId,
                                               @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        SubjectResponseDto response = subjectService.addSubjects(dto, scheduleId, currentUser.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    @PatchMapping("/{subjectId}")
    public ResponseEntity<Result> updateSubjects(@RequestBody SubjectsUpdateRequestDto dto,
                                                 @PathVariable("subjectId") Long subjectId,
                                                 @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        SubjectResponseDto response = subjectService.updateSubjects(dto, subjectId, currentUser.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    @DeleteMapping("/{scheduleId}/{subjectId}")
    public ResponseEntity<Result> deleteSubjects(@PathVariable Long scheduleId,
                                                 @PathVariable Long subjectId,
                                                 @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        SubjectsDeleteResponseDto response = subjectService.deleteSubjects(scheduleId, subjectId, currentUser.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    // 시간표에서 특정 요일[하루]에 대한 데이터 조회
    @GetMapping("/{scheduleId}/{day}")
    public ResponseEntity<Result> findDay(@PathVariable Long scheduleId,
                                          @PathVariable ClassDay day,
                                          @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        List<SubjectResponseDto> response = subjectService.getMyClass(scheduleId, day, currentUser.getId());
        return ResponseEntity.ok(Result.of(response));
    }

    // 특정 과목 조회
    @GetMapping("/detail/{scheduleId}/{subjectId}")
    public ResponseEntity<Result> findSubject(@PathVariable Long scheduleId, @PathVariable Long subjectId,
                                              @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);
        SubjectDetailResponseDto response = subjectService.getSubject(scheduleId, subjectId, currentUser.getId());

        return ResponseEntity.ok(Result.of(response));
    }
}
