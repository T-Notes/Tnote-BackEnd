package com.example.tnote.boundedContext.archive.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.base.utils.TokenUtils;
import com.example.tnote.boundedContext.archive.constant.DateType;
import com.example.tnote.boundedContext.archive.constant.LogType;
import com.example.tnote.boundedContext.archive.dto.ArchiveSliceResponseDto;
import com.example.tnote.boundedContext.archive.dto.LogsDeleteRequest;
import com.example.tnote.boundedContext.archive.service.ArchiveService;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.dto.SemesterResponse;
import com.example.tnote.boundedContext.schedule.service.ScheduleService;
import com.example.tnote.boundedContext.user.entity.auth.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tnote/v1/archive")
@Tag(name = "Archive", description = "Archive API")
public class ArchiveController {

    private final ArchiveService archiveService;
    private final ScheduleService scheduleService;
    private final RecentLogService recentLogService;

    public ArchiveController(final ArchiveService archiveService, final ScheduleService scheduleService,
                             final RecentLogService recentLogService) {
        this.archiveService = archiveService;
        this.scheduleService = scheduleService;
        this.recentLogService = recentLogService;
    }

    // 학생 이름 검색 했을때 나올 내용 - keyword로 통합
    @GetMapping("/searching/{scheduleId}")
    @Operation(summary = "find specific Archive api", description = "특정 학기 Archive 조 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> findAll(
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @AuthenticationPrincipal PrincipalDetails user, @PathVariable Long scheduleId) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);
        //ArchiveResponse response = archiveService.findAll(currentUser.getId(), scheduleId, keyword);
        List<Object> response = archiveService.findAll(currentUser.getId(), scheduleId, keyword);

        return ResponseEntity.ok(Result.of(response));
    }

    // 아카이브 명 검색 ( = 학기명 검색 )
    @GetMapping("/semester")
    @Operation(summary = "search archive name api", description = "아카이브명 검색 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SemesterResponse.class)))),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
    public ResponseEntity<Result> findSemester(
            @RequestParam(name = "semesterName", required = false, defaultValue = "") String semesterName,
            @AuthenticationPrincipal PrincipalDetails user) {

        PrincipalDetails currentUser = TokenUtils.checkValidToken(user);
        List<SemesterResponse> response = scheduleService.searchSemester(semesterName, currentUser.getId());

        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/{scheduleId}/dateLogs")
    public ResponseEntity<Result> readDateLogs(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                               @PathVariable Long scheduleId,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                               @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                               @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                               @RequestParam(value = "size", required = false, defaultValue = "8") int size,
                                               @RequestParam(value = "logType", required = false, defaultValue = "ALL") LogType logType) {

        PageRequest pageRequest = PageRequest.of(page, size);
        ArchiveSliceResponseDto response = archiveService.readLogsByDate(principalDetails.getId(), scheduleId,
                startDate,
                endDate, logType, pageRequest);
        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/{scheduleId}/daily")
    public ResponseEntity<Result> findDaily(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                            @PathVariable final Long scheduleId,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return ResponseEntity.ok(Result.of(archiveService.findDaily(principalDetails.getId(), scheduleId, date)));
    }

    @GetMapping("/{scheduleId}/monthly")
    public ResponseEntity<Result> findMonthly(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                              @PathVariable final Long scheduleId,
                                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return ResponseEntity.ok(Result.of(archiveService.findMonthly(principalDetails.getId(), scheduleId, date)));
    }

    @GetMapping("/recentLogs/{scheduleId}")
    public ResponseEntity<Result> findRecentLogs(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                 @PathVariable final Long scheduleId) {

        return ResponseEntity.ok(Result.of(recentLogService.find(principalDetails.getId(), scheduleId)));
    }

    @GetMapping("/{scheduleId}/LogsByFilter")
    public ResponseEntity<Result> findByLogType(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                                @PathVariable final Long scheduleId,
                                                @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                @RequestParam(value = "size", required = false, defaultValue = "8") int size,
                                                @RequestParam(value = "logType", required = false, defaultValue = "ALL") LogType logType) {

        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(Result.of(archiveService.findByLogType(principalDetails.getId(), scheduleId, logType,
                pageRequest)));
    }

    @PostMapping("/deleteLogs")
    public ResponseEntity<Result> delete(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                         @RequestBody final LogsDeleteRequest request) {

        return ResponseEntity.ok(Result.of(archiveService.deleteLogs(principalDetails.getId(), request)));
    }

    @GetMapping("/searching/log")
    public ResponseEntity<Result> search(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                         @RequestParam(value = "dateType", required = false, defaultValue = "ALL") final DateType dateType,
                                         @RequestParam(value = "searchType", required = false, defaultValue = "title") final String searchType,
                                         @RequestParam(value = "keyword", required = false, defaultValue = "") final String keyword,
                                         @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                         @RequestParam(value = "size", required = false, defaultValue = "8") int size) {

        PageRequest pageRequest = PageRequest.of(page, size);

        return ResponseEntity.ok(Result.of(archiveService.searchByFilter(principalDetails.getId(), dateType,
                searchType, keyword, pageRequest)));
    }
}
