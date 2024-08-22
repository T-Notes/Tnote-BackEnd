package com.example.tnote.boundedContext.archive.controller;

import com.example.tnote.base.response.Result;
import com.example.tnote.base.utils.TokenUtils;
import com.example.tnote.boundedContext.archive.constant.DateType;
import com.example.tnote.boundedContext.archive.constant.LogType;
import com.example.tnote.boundedContext.archive.dto.ArchiveResponseDto;
import com.example.tnote.boundedContext.archive.dto.ArchiveSliceResponseDto;
import com.example.tnote.boundedContext.archive.dto.LogsDeleteRequestDto;
import com.example.tnote.boundedContext.archive.dto.LogsDeleteResponseDto;
import com.example.tnote.boundedContext.archive.dto.UnifiedLogResponseDto;
import com.example.tnote.boundedContext.archive.service.ArchiveService;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponse;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponse;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.dto.SemesterNameResponseDto;
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
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@RestController
@RequestMapping("/tnote/archive")
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

        List<ConsultationResponseDto> consultation = archiveService.findAllOfConsultation(keyword, currentUser.getId(),
                scheduleId);
        List<ObservationResponseDto> observation = archiveService.findAllOfObservation(keyword, currentUser.getId(),
                scheduleId);
        List<ClassLogResponse> classLog = archiveService.findAllOfClassLog(keyword, currentUser.getId(), scheduleId);
        List<ProceedingResponse> proceeding = archiveService.findAllOfProceeding(keyword, currentUser.getId(),
                scheduleId);

        List<Object> response = new ArrayList<>();
        response.addAll(consultation);
        response.addAll(observation);
        response.addAll(classLog);
        response.addAll(proceeding);

        return ResponseEntity.ok(Result.of(response));
    }

    // 아카이브 명 검색 ( = 학기명 검색 )
    @GetMapping("/semester")
    @Operation(summary = "search archive name api", description = "아카이브명 검색 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SemesterNameResponseDto.class)))),
            @ApiResponse(responseCode = "404", description = "로그인 실패")
    })
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
                                               @RequestParam(value = "logType", required = false, defaultValue = "ALL") LogType logType) {

        PageRequest pageRequest = PageRequest.of(page, size);
        ArchiveSliceResponseDto response = archiveService.readLogsByDate(principalDetails.getId(), scheduleId,
                startDate,
                endDate, logType, pageRequest);
        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/{scheduleId}/dailyLogs")
    public ResponseEntity<Result> findDaily(@AuthenticationPrincipal final PrincipalDetails principalDetails,
                                            @PathVariable final Long scheduleId,
                                            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        return ResponseEntity.ok(Result.of(archiveService.readDailyLogs(principalDetails.getId(), scheduleId, date)));
    }

    @GetMapping("/{scheduleId}/monthlyLogs")
    public ResponseEntity<Result> readMonthlyLogs(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                  @PathVariable Long scheduleId,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date == null) {
            date = LocalDate.now();
        }
        ArchiveResponseDto response = archiveService.readMonthlyLogs(principalDetails.getId(), scheduleId, date);
        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/recentLogs/{scheduleId}")
    public ResponseEntity<Result> getRecentClassLogs(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                     @PathVariable Long scheduleId) {

        return ResponseEntity.ok(Result.of(recentLogService.find(principalDetails.getId(), scheduleId)));
    }

    @GetMapping("/{scheduleId}/LogsByFilter")
    public ResponseEntity<Result> readLogsByFilter(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                   @PathVariable Long scheduleId,
                                                   @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                   @RequestParam(value = "size", required = false, defaultValue = "8") int size,
                                                   @RequestParam(value = "logType", required = false, defaultValue = "ALL") LogType logType) {

        PageRequest pageRequest = PageRequest.of(page, size);
        UnifiedLogResponseDto response = archiveService.readLogByFilter(principalDetails.getId(), scheduleId, logType,
                pageRequest);
        return ResponseEntity.ok(Result.of(response));
    }

    @PostMapping("/deleteLogs")
    public ResponseEntity<Result> deleteLogs(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                             @RequestBody LogsDeleteRequestDto deleteRequest) {
        LogsDeleteResponseDto response = archiveService.deleteLogs(principalDetails.getId(), deleteRequest);
        return ResponseEntity.ok(Result.of(response));
    }

    @GetMapping("/searching/log")
    public ResponseEntity<Result> searchLogsByFilter(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                     @RequestParam(value = "dateType", required = false, defaultValue = "ALL") DateType dateType,
                                                     @RequestParam(value = "searchType", required = false, defaultValue = "title") String searchType,
                                                     @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
                                                     @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "8") int size) {

        PageRequest pageRequest = PageRequest.of(page, size);
        UnifiedLogResponseDto response = archiveService.searchLogsByFilter(principalDetails.getId(), dateType,
                searchType, keyword, pageRequest);

        return ResponseEntity.ok(Result.of(response));
    }
}
