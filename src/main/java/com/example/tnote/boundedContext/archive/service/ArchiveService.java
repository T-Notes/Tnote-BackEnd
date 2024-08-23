package com.example.tnote.boundedContext.archive.service;

import static com.example.tnote.base.utils.DateUtils.calculateStartDate;
import static com.example.tnote.boundedContext.user.exception.UserErrorCode.USER_NOT_FOUND;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.boundedContext.archive.constant.DateType;
import com.example.tnote.boundedContext.archive.constant.LogType;
import com.example.tnote.boundedContext.archive.dto.ArchiveResponse;
import com.example.tnote.boundedContext.archive.dto.ArchiveSliceResponseDto;
import com.example.tnote.boundedContext.archive.dto.LogEntry;
import com.example.tnote.boundedContext.archive.dto.LogsDeleteRequestDto;
import com.example.tnote.boundedContext.archive.dto.LogsDeleteResponseDto;
import com.example.tnote.boundedContext.archive.dto.UnifiedLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponse;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponses;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.repository.query.ClassLogQueryRepository;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSliceResponseDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.repository.query.ConsultationQueryRepository;
import com.example.tnote.boundedContext.consultation.service.ConsultationService;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationSliceResponseDto;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.repository.query.ObservationQueryRepository;
import com.example.tnote.boundedContext.observation.service.ObservationService;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponse;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponses;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.repository.query.ProceedingQueryRepository;
import com.example.tnote.boundedContext.proceeding.service.ProceedingService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.exception.ScheduleErrorCode;
import com.example.tnote.boundedContext.schedule.exception.ScheduleException;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.todo.dto.TodoResponseDto;
import com.example.tnote.boundedContext.todo.service.TodoService;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArchiveService {

    private final ConsultationQueryRepository consultationQueryRepository;
    private final ObservationQueryRepository observationQueryRepository;
    private final ClassLogQueryRepository classLogQueryRepository;
    private final ProceedingQueryRepository proceedingQueryRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ClassLogService classLogService;
    private final ProceedingService proceedingService;
    private final ConsultationService consultationService;
    private final ObservationService observationService;
    private final TodoService todoService;

    @Transactional(readOnly = true)
    public List<ConsultationResponseDto> findAllOfConsultation(String studentName, Long userId, Long scheduleId) {

        findUser(userId);

        List<Consultation> consultations = consultationQueryRepository.findAll(studentName, scheduleId);

        return consultations.stream()
                .map(ConsultationResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ObservationResponseDto> findAllOfObservation(String studentName, Long userId, Long scheduleId) {

        findUser(userId);

        List<Observation> observations = observationQueryRepository.findAll(studentName, scheduleId);

        return observations.stream()
                .map(ObservationResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassLogResponse> findAllOfClassLog(String title, Long userId, Long scheduleId) {

        findUser(userId);

        List<ClassLog> classLogs = classLogQueryRepository.findAll(title, scheduleId);

        return classLogs.stream()
                .map(ClassLogResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProceedingResponse> findAllOfProceeding(String title, Long userId, Long scheduleId) {

        findUser(userId);

        List<Proceeding> proceedings = proceedingQueryRepository.findAll(title, scheduleId);

        return proceedings.stream()
                .map(ProceedingResponse::from)
                .toList();
    }

    private void findUser(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new CustomException(USER_NOT_FOUND));
    }

    public ArchiveSliceResponseDto readLogsByDate(Long userId, Long scheduleId, LocalDate startDate, LocalDate endDate,
                                                  LogType logType, Pageable pageable) {
        if (logType == LogType.CLASS_LOG) {
            ClassLogResponses classLogs = classLogService.findByDate(userId, scheduleId, startDate,
                    endDate, pageable);
            return ArchiveSliceResponseDto.builder().classLogs(classLogs).build();
        }
        if (logType == LogType.CONSULTATION) {
            ConsultationSliceResponseDto consultations = consultationService.readConsultationsByDate(userId, scheduleId,
                    startDate,
                    endDate, pageable);
            return ArchiveSliceResponseDto.builder().consultations(consultations).build();
        }
        if (logType == LogType.OBSERVATION) {
            ObservationSliceResponseDto observations = observationService.readObservationsByDate(userId, scheduleId,
                    startDate,
                    endDate, pageable);
            return ArchiveSliceResponseDto.builder().observations(observations).build();
        }
        if (logType == LogType.PROCEEDING) {
            ProceedingResponses proceedings = proceedingService.findByDate(userId, scheduleId,
                    startDate,
                    endDate, pageable);
            return ArchiveSliceResponseDto.builder().proceedings(proceedings).build();
        }
        return null;
    }

    public UnifiedLogResponseDto readLogByFilter(Long userId, Long scheduleId, LogType logType, Pageable pageable) {
        List<LogEntry> logs = new ArrayList<>();

        if (logType == LogType.ALL || logType == LogType.CLASS_LOG) {
            logs.addAll(classLogService.findByScheduleAndUser(scheduleId, userId));
        }
        if (logType == LogType.ALL || logType == LogType.CONSULTATION) {
            logs.addAll(consultationService.findLogsByScheduleAndUser(scheduleId, userId));
        }
        if (logType == LogType.ALL || logType == LogType.OBSERVATION) {
            logs.addAll(observationService.findLogsByScheduleAndUser(scheduleId, userId));
        }
        if (logType == LogType.ALL || logType == LogType.PROCEEDING) {
            logs.addAll(proceedingService.findLogsByScheduleAndUser(scheduleId, userId));
        }

        int totalLogs = logs.size();
        System.out.println("Total logs fetched: " + totalLogs);
        logs.sort(Comparator.comparing(LogEntry::getCreatedAt).reversed());

        int start = (int) pageable.getOffset();
        if (start >= totalLogs) {
            System.out.println("Start index exceeds the log list size.");
            return UnifiedLogResponseDto.from(Collections.emptyList(), totalLogs);
        }

        int end = Math.min((start + pageable.getPageSize()), totalLogs);
        System.out.println("Returning logs from index " + start + " to " + end);
        List<LogEntry> pageContent = logs.subList(start, end);

        return UnifiedLogResponseDto.from(pageContent, totalLogs);
    }

    public UnifiedLogResponseDto searchLogsByFilter(Long userId, DateType dateType,
                                                    String searchType, String keyword, Pageable pageable) {
        List<LogEntry> logs = new ArrayList<>();
        LocalDate startDate = calculateStartDate(dateType);
        LocalDate endDate = LocalDate.now();

        if ("title".equals(searchType)) {
            logs.addAll(classLogService.findByTitle(keyword, startDate, endDate, userId));
            logs.addAll(consultationService.findByTitle(keyword, startDate, endDate, userId));
            logs.addAll(proceedingService.findByTitle(keyword, startDate, endDate, userId));
            logs.addAll(observationService.findByTitle(keyword, startDate, endDate, userId));
        }
        if ("content".equals((searchType))) {
            logs.addAll(classLogService.findByContents(keyword, startDate, endDate, userId));
            logs.addAll(consultationService.findByContents(keyword, startDate, endDate, userId));
            logs.addAll(proceedingService.findByContents(keyword, startDate, endDate, userId));
            logs.addAll(observationService.findByContents(keyword, startDate, endDate, userId));
        }
        if ("titleAndContent".equals(searchType)) {
            logs.addAll(classLogService.findByTitleOrPlanOrContents(keyword, startDate,
                    endDate, userId));
            logs.addAll(consultationService.findByTitleOrPlanOrContents(keyword, startDate,
                    endDate, userId));
            logs.addAll(proceedingService.findByTitleOrPlanOrContents(keyword, startDate,
                    endDate, userId));
            logs.addAll(observationService.findByTitleOrPlanOrContents(keyword, startDate,
                    endDate, userId));
        }

        int totalLogs = logs.size();
        System.out.println("Total logs fetched: " + totalLogs);
        logs.sort(Comparator.comparing(LogEntry::getCreatedAt).reversed());

        int start = (int) pageable.getOffset();
        if (start >= totalLogs) {
            System.out.println("Start index exceeds the log list size.");
            return UnifiedLogResponseDto.from(Collections.emptyList(), totalLogs);
        }

        int end = Math.min((start + pageable.getPageSize()), totalLogs);
        System.out.println("Returning logs from index " + start + " to " + end);
        List<LogEntry> pageContent = logs.subList(start, end);

        return UnifiedLogResponseDto.from(pageContent, totalLogs);
    }

    public ArchiveResponse findDaily(final Long userId, final Long scheduleId, final LocalDate date) {
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        if (date.isBefore(schedule.getStartDate()) || (date.isAfter(schedule.getEndDate()))) {
            throw new ScheduleException(ScheduleErrorCode.DATES_NOT_INCLUDED_IN_SEMESTER);
        }
        List<ClassLogResponse> classLogs = classLogService.findDaily(userId, scheduleId, date);
        List<ConsultationResponseDto> consultations = consultationService.readDailyConsultations(userId, scheduleId,
                date);
        List<ObservationResponseDto> observations = observationService.readDailyObservations(userId, scheduleId, date);
        List<ProceedingResponse> proceedings = proceedingService.findDaily(userId, scheduleId, date);
        List<TodoResponseDto> todos = todoService.readDailyTodos(userId, scheduleId, date);

        return ArchiveResponse.builder()
                .classLogs(classLogs)
                .consultations(consultations)
                .observations(observations)
                .proceedings(proceedings)
                .todos(todos)
                .build();
    }

    public ArchiveResponse readMonthlyLogs(Long userId, Long scheduleId, LocalDate date) {
        List<ClassLogResponse> classLogs = classLogService.findMonthly(userId, scheduleId, date);
        List<ConsultationResponseDto> consultations = consultationService.readMonthlyConsultations(userId, scheduleId,
                date);
        List<ObservationResponseDto> observations = observationService.readMonthlyObservations(userId, scheduleId,
                date);
        List<ProceedingResponse> proceedings = proceedingService.findMonthly(userId, scheduleId, date);
        List<TodoResponseDto> todos = todoService.readMonthlyTodos(userId, scheduleId, date);

        return ArchiveResponse.builder()
                .classLogs(classLogs)
                .consultations(consultations)
                .observations(observations)
                .proceedings(proceedings)
                .todos(todos)
                .build();
    }

    @Transactional
    public LogsDeleteResponseDto deleteLogs(Long userId, LogsDeleteRequestDto deleteRequest) {
        int deletedClassLogsCount = 0;
        int deletedProceedingsCount = 0;
        int deletedObservationsCount = 0;
        int deletedConsultationsCount = 0;

        if (!deleteRequest.getClassLogIds().isEmpty()) {
            deletedClassLogsCount = classLogService.deleteClassLogs(userId, deleteRequest.getClassLogIds());
        }
        if (!deleteRequest.getProceedingIds().isEmpty()) {
            deletedProceedingsCount = proceedingService.deleteProceedings(userId, deleteRequest.getProceedingIds());
        }
        if (!deleteRequest.getObservationIds().isEmpty()) {
            deletedObservationsCount = observationService.deleteObservations(userId, deleteRequest.getObservationIds());
        }
        if (!deleteRequest.getConsultationIds().isEmpty()) {
            deletedConsultationsCount = consultationService.deleteConsultations(userId,
                    deleteRequest.getConsultationIds());
        }

        return LogsDeleteResponseDto.of(deletedClassLogsCount, deletedProceedingsCount, deletedObservationsCount,
                deletedConsultationsCount);
    }


}
