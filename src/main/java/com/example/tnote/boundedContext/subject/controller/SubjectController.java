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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Subject", description = "Subject API")
public class SubjectController {

    private final SubjectService subjectService;

    @PostMapping("/{scheduleId}")
    @Operation(summary = "create Subject api", description = "accessToken로 Subject 생성 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = {@Content(schema = @Schema(implementation = SubjectResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> saveSubjects(@RequestBody final SubjectRequestDto dto,
                                               @PathVariable("scheduleId") final Long scheduleId,
                                               @AuthenticationPrincipal final PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        SubjectResponseDto response = subjectService.addSubjects(dto, scheduleId, currentUser.getId());

        return ResponseEntity.ok(Result.of(response));
    }


    @PatchMapping("/{subjectId}")
    @Operation(summary = "update Subject api", description = "accessToken로 Subject 수정 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = {@Content(schema = @Schema(implementation = SubjectResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> updateSubjects(@RequestBody final SubjectsUpdateRequestDto dto,
                                                 @PathVariable("subjectId") final Long subjectId,
                                                 @AuthenticationPrincipal final PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        SubjectResponseDto response = subjectService.updateSubjects(dto, subjectId, currentUser.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    @DeleteMapping("/{scheduleId}/{subjectId}")
    @Operation(summary = "delete Subject api", description = "accessToken로 Subject 삭제 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = {@Content(schema = @Schema(implementation = SubjectsDeleteResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> deleteSubjects(@PathVariable final Long scheduleId,
                                                 @PathVariable final Long subjectId,
                                                 @AuthenticationPrincipal final PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        SubjectsDeleteResponseDto response = subjectService.deleteSubjects(scheduleId, subjectId, currentUser.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    // 시간표에서 특정 요일[하루]에 대한 데이터 조회
    @GetMapping("/{scheduleId}/{day}")
    @Operation(summary = "specific day's Subject api", description = "특정 요일 Subject 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SubjectResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> findDay(@PathVariable final Long scheduleId,
                                          @PathVariable final ClassDay day,
                                          @AuthenticationPrincipal final PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);

        List<SubjectResponseDto> response = subjectService.getMyClass(scheduleId, day, currentUser.getId());
        return ResponseEntity.ok(Result.of(response));
    }

    // 특정 과목 조회
    @GetMapping("/detail/{scheduleId}/{subjectId}")
    @Operation(summary = "specific Subject api", description = "특정 Subject 조회 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = {@Content(schema = @Schema(implementation = SubjectDetailResponseDto.class))}),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> findSubject(@PathVariable final Long scheduleId, @PathVariable final Long subjectId,
                                              @AuthenticationPrincipal final PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);
        SubjectDetailResponseDto response = subjectService.getSubject(scheduleId, subjectId, currentUser.getId());

        return ResponseEntity.ok(Result.of(response));
    }
}
