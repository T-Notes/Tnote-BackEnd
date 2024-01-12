package com.example.tnote.boundedContext.subject.controller;

import com.example.tnote.base.exception.CommonErrorResult;
import com.example.tnote.base.exception.CommonException;
import com.example.tnote.base.response.Result;
import com.example.tnote.boundedContext.schedule.entity.ClassDay;
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
@RequestMapping("/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping
    public ResponseEntity<Result> saveSubjects(@RequestBody SubjectRequestDto dto,
                                               @AuthenticationPrincipal PrincipalDetails user) {

        if (user == null) {
            log.warn("user is not authorized");
            throw new CommonException(CommonErrorResult.UNAUTHORIZED);
        }
        SubjectResponseDto response = subjectService.addSubjects(dto);

        return ResponseEntity.ok(Result.of(response));
    }

    @PatchMapping("/{subjectsId}")
    public ResponseEntity<Result> updateSubjects(@RequestBody SubjectsUpdateRequestDto dto,
                                                 @PathVariable("subjectsId") Long subjectsId,
                                                 @AuthenticationPrincipal PrincipalDetails user) {

        SubjectResponseDto response = subjectService.updateSubjects(dto, subjectsId, user);

        return ResponseEntity.ok(Result.of(response));
    }

    @DeleteMapping("/{scheduleId}/{subjectsId}")
    public ResponseEntity<Result> deleteSubjects(@PathVariable Long scheduleId,
                                                 @PathVariable Long subjectsId,
                                                 @AuthenticationPrincipal PrincipalDetails user) {

        SubjectsDeleteResponseDto response = subjectService.deleteSubjects(scheduleId, subjectsId, user);

        return ResponseEntity.ok(Result.of(response));
    }

    // 특정 요일에 대한 데이터 조회
    @GetMapping("/details/{scheduleId}/{day}")
    public ResponseEntity<Result> findDay(@PathVariable Long scheduleId,
                                          @PathVariable ClassDay day,
                                          @AuthenticationPrincipal PrincipalDetails user) {

        List<SubjectResponseDto> response = subjectService.getMyClass(scheduleId, day, user);
        return ResponseEntity.ok(Result.of(response));
    }
}
