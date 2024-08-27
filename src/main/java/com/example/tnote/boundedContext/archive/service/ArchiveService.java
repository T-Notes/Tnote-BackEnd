package com.example.tnote.boundedContext.archive.service;

import static com.example.tnote.base.utils.DateUtils.calculateStartDate;

import com.example.tnote.boundedContext.archive.constant.DateType;
import com.example.tnote.boundedContext.archive.constant.LogType;
import com.example.tnote.boundedContext.archive.dto.ArchiveResponse;
import com.example.tnote.boundedContext.archive.dto.ArchiveSliceResponseDto;
import com.example.tnote.boundedContext.archive.dto.LogEntry;
import com.example.tnote.boundedContext.archive.dto.LogsDeleteRequest;
import com.example.tnote.boundedContext.archive.dto.LogsDeleteResponse;
import com.example.tnote.boundedContext.archive.dto.UnifiedLogResponse;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponse;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponses;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.repository.query.ClassLogQueryRepository;
import com.example.tnote.boundedContext.classLog.service.ClassLogService;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponse;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSliceResponseDto;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.repository.query.ConsultationQueryRepository;
import com.example.tnote.boundedContext.consultation.service.ConsultationService;
import com.example.tnote.boundedContext.observation.dto.ObservationResponse;
import com.example.tnote.boundedContext.observation.dto.ObservationResponses;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.repository.query.ObservationQueryRepository;
import com.example.tnote.boundedContext.observation.service.ObservationService;
import com.example.tnote.boundedContext.plan.dto.PlanResponse;
import com.example.tnote.boundedContext.plan.service.PlanService;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponse;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponses;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.repository.query.ProceedingQueryRepository;
import com.example.tnote.boundedContext.proceeding.service.ProceedingService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.exception.ScheduleErrorCode;
import com.example.tnote.boundedContext.schedule.exception.ScheduleException;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.todo.dto.TodoResponse;
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
@Transactional(readOnly = true)
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
    private final PlanService planService;

    public List<ConsultationResponse> findAllOfConsultation(String studentName, Long userId, Long scheduleId) {

        userRepository.findById(userId);

        List<Consultation> consultations = consultationQueryRepository.findAll(studentName, scheduleId);

        return consultations.stream()
                .map(ConsultationResponse::from)
                .toList();
    }

    public List<ObservationResponse> findAllOfObservation(String studentName, Long userId, Long scheduleId) {

        userRepository.findById(userId);

        List<Observation> observations = observationQueryRepository.findAll(studentName, scheduleId);

        return observations.stream()
                .map(ObservationResponse::from)
                .toList();
    }

    public List<ClassLogResponse> findAllOfClassLog(String title, Long userId, Long scheduleId) {

        userRepository.findById(userId);

        List<ClassLog> classLogs = classLogQueryRepository.findAll(title, scheduleId);

        return classLogs.stream()
                .map(ClassLogResponse::from)
                .toList();
    }

    public List<ProceedingResponse> findAllOfProceeding(String title, Long userId, Long scheduleId) {

        userRepository.findById(userId);

        List<Proceeding> proceedings = proceedingQueryRepository.findAll(title, scheduleId);

        return proceedings.stream()
                .map(ProceedingResponse::from)
                .toList();
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
            ObservationResponses observations = observationService.readObservationsByDate(userId, scheduleId,
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

    public UnifiedLogResponse findByLogType(final Long userId, final Long scheduleId, final LogType logType,
                                            final Pageable pageable) {
        List<LogEntry> logs = new ArrayList<>();

        if (logType == LogType.ALL || logType == LogType.CLASS_LOG) {
            logs.addAll(classLogService.findByScheduleAndUser(scheduleId, userId));
        }
        if (logType == LogType.ALL || logType == LogType.CONSULTATION) {
            logs.addAll(consultationService.findByScheduleAndUser(scheduleId, userId));
        }
        if (logType == LogType.ALL || logType == LogType.OBSERVATION) {
            logs.addAll(observationService.findByScheduleAndUser(scheduleId, userId));
        }
        if (logType == LogType.ALL || logType == LogType.PROCEEDING) {
            logs.addAll(proceedingService.findByScheduleAndUser(scheduleId, userId));
        }
        if (logType == LogType.ALL || logType == LogType.PLAN) {
            logs.addAll(planService.findByScheduleAndUser(scheduleId, userId));
        }

        return processLogs(logs, pageable);
    }

    public UnifiedLogResponse searchByFilter(final Long userId, final DateType dateType,
                                             final String searchType, final String keyword,
                                             final Pageable pageable) {
        List<LogEntry> logs = new ArrayList<>();
        LocalDate startDate = calculateStartDate(dateType);
        LocalDate endDate = LocalDate.now();

        logs.addAll(classLogService.findByFilter(userId, startDate, endDate, searchType, keyword));
        logs.addAll(consultationService.findByFilter(userId, startDate, endDate, searchType, keyword));
        logs.addAll(observationService.findByFilter(userId, startDate, endDate, searchType, keyword));
        logs.addAll(proceedingService.findByFilter(userId, startDate, endDate, searchType, keyword));
        logs.addAll(planService.findByFilter(userId, startDate, endDate, searchType, keyword));

        return processLogs(logs, pageable);
    }

    public ArchiveResponse findDaily(final Long userId, final Long scheduleId, final LocalDate date) {
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        validateDateWithinSchedule(date, schedule);
        List<ClassLogResponse> classLogs = classLogService.findDaily(userId, scheduleId, date);
        List<ConsultationResponse> consultations = consultationService.readDailyConsultations(userId, scheduleId,
                date);
        List<ObservationResponse> observations = observationService.findDaily(userId, scheduleId, date);
        List<ProceedingResponse> proceedings = proceedingService.findDaily(userId, scheduleId, date);
        List<TodoResponse> todos = todoService.readDailyTodos(userId, scheduleId, date);
        List<PlanResponse> plans = planService.findDaily(userId, scheduleId, date);

        return ArchiveResponse.of(classLogs, consultations, observations, proceedings, todos, plans);
    }

    public ArchiveResponse findMonthly(final Long userId, final Long scheduleId, final LocalDate date) {
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        validateDateWithinSchedule(date, schedule);

        List<ClassLogResponse> classLogs = classLogService.findMonthly(userId, scheduleId, date);
        List<ConsultationResponse> consultations = consultationService.readMonthlyConsultations(userId, scheduleId,
                date);
        List<ObservationResponse> observations = observationService.findMonthly(userId, scheduleId,
                date);
        List<ProceedingResponse> proceedings = proceedingService.findMonthly(userId, scheduleId, date);
        List<TodoResponse> todos = todoService.readMonthlyTodos(userId, scheduleId, date);
        List<PlanResponse> plans = planService.findMonthly(userId, scheduleId, date);

        return ArchiveResponse.of(classLogs, consultations, observations, proceedings, todos, plans);
    }

    @Transactional
    public LogsDeleteResponse deleteLogs(final Long userId, final LogsDeleteRequest request) {
        int deletedClassLogsCount = 0;
        int deletedProceedingsCount = 0;
        int deletedObservationsCount = 0;
        int deletedConsultationsCount = 0;
        int deletedPlanCount = 0;

        if (!request.getClassLogIds().isEmpty()) {
            deletedClassLogsCount = classLogService.deleteClassLogs(userId, request.getClassLogIds());
        }
        if (!request.getProceedingIds().isEmpty()) {
            deletedProceedingsCount = proceedingService.deleteProceedings(userId, request.getProceedingIds());
        }
        if (!request.getObservationIds().isEmpty()) {
            deletedObservationsCount = observationService.deleteObservations(userId, request.getObservationIds());
        }
        if (!request.getConsultationIds().isEmpty()) {
            deletedConsultationsCount = consultationService.deleteConsultations(userId,
                    request.getConsultationIds());
        }
        if (!request.getPlanIds().isEmpty()) {
            deletedPlanCount = planService.deletePlans(userId, request.getPlanIds());
        }

        return LogsDeleteResponse.of(deletedClassLogsCount, deletedProceedingsCount, deletedObservationsCount,
                deletedConsultationsCount, deletedPlanCount);
    }

    private void validateDateWithinSchedule(final LocalDate date, final Schedule schedule) {
        if (date.isBefore(schedule.getStartDate()) || date.isAfter(schedule.getEndDate())) {
            throw new ScheduleException(ScheduleErrorCode.DATES_NOT_INCLUDED_IN_SEMESTER);
        }
    }

    private UnifiedLogResponse processLogs(final List<LogEntry> logs, final Pageable pageable) {
        int totalLogs = logs.size();
        System.out.println("Total logs fetched: " + totalLogs);
        logs.sort(Comparator.comparing(LogEntry::getCreatedAt).reversed());

        int start = (int) pageable.getOffset();
        if (start >= totalLogs) {
            System.out.println("Start index exceeds the log list size.");
            return UnifiedLogResponse.of(Collections.emptyList(), totalLogs);
        }

        int end = Math.min((start + pageable.getPageSize()), totalLogs);
        System.out.println("Returning logs from index " + start + " to " + end);
        List<LogEntry> pageContent = logs.subList(start, end);

        return UnifiedLogResponse.of(pageContent, totalLogs);
    }


}
