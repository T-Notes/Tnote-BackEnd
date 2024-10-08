package com.example.tnote.boundedContext.classLog.service;

import com.example.tnote.base.utils.AwsS3Uploader;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.archive.constant.DateType;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponse;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSaveRequest;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponse;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponses;
import com.example.tnote.boundedContext.classLog.dto.ClassLogUpdateRequest;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import com.example.tnote.boundedContext.classLog.exception.ClassLogErrorCode;
import com.example.tnote.boundedContext.classLog.exception.ClassLogException;
import com.example.tnote.boundedContext.classLog.repository.ClassLogImageRepository;
import com.example.tnote.boundedContext.classLog.repository.ClassLogRepository;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional(readOnly = true)
@Service
@Slf4j
public class ClassLogService {
    private final ClassLogRepository classLogRepository;
    private final ClassLogImageRepository classLogImageRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final RecentLogService recentLogService;
    private final AwsS3Uploader awsS3Uploader;

    public ClassLogService(final ClassLogRepository classLogRepository,
                           final ClassLogImageRepository classLogImageRepository,
                           final UserRepository userRepository, final ScheduleRepository scheduleRepository,
                           final RecentLogService recentLogService, final AwsS3Uploader awsS3Uploader) {
        this.classLogRepository = classLogRepository;
        this.classLogImageRepository = classLogImageRepository;
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.recentLogService = recentLogService;
        this.awsS3Uploader = awsS3Uploader;
    }

    @Transactional
    public ClassLogResponse save(final Long userId, final Long scheduleId, final ClassLogSaveRequest request,
                                 final List<MultipartFile> classLogImages) {
        User user = userRepository.findUserById(userId);
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        validateIncorrectTime(request, schedule);
        ClassLog classLog = classLogRepository.save(request.toEntity(user, schedule));

        if (classLogImages != null && !classLogImages.isEmpty()) {
            List<ClassLogImage> uploadedImages = uploadImages(classLog, classLogImages);
            classLog.getClassLogImage().addAll(uploadedImages);
        }
        recentLogService.save(userId, classLog.getId(), scheduleId, "CLASS_LOG");
        return ClassLogResponse.from(classLog);
    }

    @Transactional
    public ClassLogDeleteResponse delete(final Long userId, final Long classLogId) {
        ClassLog classLog = findByIdAndUserId(classLogId, userId);

        deleteExistedImage(classLog);
        classLogRepository.delete(classLog);
        recentLogService.delete(classLog.getId(), "CLASS_LOG");

        return ClassLogDeleteResponse.from(classLog);
    }

    @Transactional
    public int deleteClassLogs(final Long userId, final List<Long> classLogIds) {
        classLogIds.forEach(classLogId -> delete(userId, classLogId));
        return classLogIds.size();
    }

    public ClassLogResponses findAll(final Long userId, final Long scheduleId, final Pageable pageable) {
        List<ClassLog> classLogList = classLogRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        Slice<ClassLog> allClassLogsSlice = classLogRepository.findAllByScheduleId(scheduleId, pageable);
        List<ClassLogResponse> classLogResponseDtos = allClassLogsSlice.getContent().stream()
                .map(ClassLogResponse::from).toList();

        return ClassLogResponses.of(classLogResponseDtos, classLogList, allClassLogsSlice);
    }

    public List<ClassLogResponse> findByScheduleAndUser(final Long scheduleId, final Long userId) {
        List<ClassLog> logs = classLogRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        return logs.stream()
                .map(ClassLogResponse::from)
                .toList();
    }

    public List<ClassLogResponse> findByFilter(final Long userId, final LocalDate startDate, final LocalDate endDate,
                                               final String searchType, final String keyword) {
        if ("title".equals(searchType)) {
            return findByTitle(keyword, startDate, endDate, userId);
        }
        if ("content".equals((searchType))) {
            return findByContents(keyword, startDate, endDate, userId);
        }
        if ("titleAndContent".equals(searchType)){
            return findByTitleOrPlanOrContents(keyword, startDate, endDate, userId);
        }
        return null;
    }

    private List<ClassLogResponse> findByTitle(final String keyword, final LocalDate startDate,
                                              final LocalDate endDate, final Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<ClassLog> logs = classLogRepository.findByTitleContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ClassLogResponse::from)
                .toList();
    }

    private List<ClassLogResponse> findByContents(final String keyword, final LocalDate startDate,
                                                 final LocalDate endDate, final Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<ClassLog> logs = classLogRepository.findByContentsContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ClassLogResponse::from)
                .toList();
    }

    private List<ClassLogResponse> findByTitleOrPlanOrContents(final String keyword, final LocalDate startDate,
                                                              final LocalDate endDate, final Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<ClassLog> logs = classLogRepository.findByTitleOrPlanOrClassContentsContaining(keyword,
                startOfDay, endOfDay, userId);

        return logs.stream()
                .map(ClassLogResponse::from)
                .toList();
    }

    @Transactional
    public ClassLogResponse find(final Long userId, final Long classLogId) {
        ClassLog classLog = findByIdAndUserId(classLogId, userId);
        recentLogService.save(userId, classLog.getId(), classLog.getSchedule().getId(), "CLASS_LOG");
        return ClassLogResponse.from(classLog);
    }

    @Transactional
    public ClassLogResponse update(final Long userId, final Long classLogId,
                                   final ClassLogUpdateRequest classLogUpdateRequestDto,
                                   final List<MultipartFile> classLogImages) {

        ClassLog classLog = findByIdAndUserId(classLogId, userId);
        updateEachItem(classLogUpdateRequestDto, classLog, classLogImages);
        recentLogService.save(userId, classLog.getId(), classLog.getSchedule().getId(), "CLASS_LOG");
        return ClassLogResponse.from(classLog);
    }

    private void updateEachItem(final ClassLogUpdateRequest request, final ClassLog classLog,
                                final List<MultipartFile> classLogImages) {
        updateFields(request, classLog);
        if (classLogImages == null || classLogImages.isEmpty()) {
            deleteExistedImages(classLog);
        }
        if (classLogImages != null && !classLogImages.isEmpty()) {
            List<ClassLogImage> updatedImages = deleteExistedImagesAndUploadNewImages(classLog, classLogImages);
            classLog.updateClassLogImages(updatedImages);
        }
    }

    private void updateFields(final ClassLogUpdateRequest request, final ClassLog classLog) {
        classLog.updateTitle(request.getTitle());
        classLog.updateStartDate(request.getStartDate());
        classLog.updateEndDate(request.getEndDate());
        classLog.updatePlan(request.getPlan());
        classLog.updateSubmission(request.getSubmission());
        classLog.updateClassContents(request.getClassContents());
        classLog.updateMagnitude(request.getMagnitude());
    }

    private List<ClassLogImage> uploadImages(final ClassLog classLog,
                                             final List<MultipartFile> classLogImages) {
        return classLogImages.stream()
                .map(file -> awsS3Uploader.upload(file, "classLog"))
                .map(pair -> createImage(classLog, pair.getFirst(), pair.getSecond()))
                .toList();
    }

    private ClassLogImage createImage(final ClassLog classLog, final String imageUrl, final String originalFileName) {
        log.info("url = {}", imageUrl);
        classLog.clearClassLogImages();

        return classLogImageRepository.save(ClassLogImage.builder()
                .originalFileName(originalFileName)
                .classLogImageUrl(imageUrl)
                .classLog(classLog)
                .build());
    }

    public ClassLogResponses findByDate(final Long userId, final Long scheduleId, final LocalDate startDate,
                                        final LocalDate endDate, final Pageable pageable) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<ClassLog> classLogList = classLogRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay, endOfDay);
        Slice<ClassLog> allClassLogsSlice = classLogRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(
                userId, scheduleId, startOfDay, endOfDay, pageable);

        List<ClassLogResponse> classLogResponseDtos = allClassLogsSlice.getContent().stream()
                .map(ClassLogResponse::from).toList();

        return ClassLogResponses.of(classLogResponseDtos, classLogList, allClassLogsSlice);
    }

    public List<ClassLogResponse> findDaily(final Long userId, final Long scheduleId, final LocalDate date) {

        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<ClassLog> classLogs = classLogRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId, scheduleId,
                startOfDay, endOfDay);

        return classLogs.stream()
                .map(ClassLogResponse::from).toList();
    }

    public List<ClassLogResponse> findMonthly(final Long userId, final Long scheduleId, final LocalDate date) {

        List<ClassLog> classLogs = classLogRepository.findByUserIdAndScheduleIdAndYearMonth(userId, scheduleId, date);

        return classLogs.stream()
                .map(ClassLogResponse::from).toList();
    }


    private List<ClassLogImage> deleteExistedImagesAndUploadNewImages(final ClassLog classLog,
                                                                      final List<MultipartFile> classLogImages) {
        deleteExistedImages(classLog);
        return uploadImages(classLog, classLogImages);
    }

    private void deleteExistedImages(final ClassLog classLog) {
        System.out.println("Deleting existing images for classLog ID: " + classLog.getId());
        deleteS3Images(classLog);
        classLogImageRepository.deleteByClassLogId(classLog.getId());
    }

    private void deleteExistedImage(final ClassLog classLog) {
        System.out.println("Deleting existing images for classLog ID: " + classLog.getId());
        deleteS3Images(classLog);
    }

    private void deleteS3Images(final ClassLog classLog) {
        System.out.println("Starting to delete images from S3 for classLog ID: " + classLog.getId());
        List<ClassLogImage> classLogImages = classLog.getClassLogImage();
        for (ClassLogImage classLogImage : classLogImages) {
            String imageKey = classLogImage.getClassLogImageUrl().substring(49);
            System.out.println("Deleting image from S3: " + imageKey);
            awsS3Uploader.deleteImage(imageKey);
        }
    }

    private ClassLog findByIdAndUserId(final Long id, final Long userId) {
        return classLogRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ClassLogException(ClassLogErrorCode.CLASS_LOG_NOT_FOUNT));
    }

    private void validateIncorrectTime(final ClassLogSaveRequest request, Schedule schedule) {
        if (request.getStartDate().toLocalDate().isBefore(schedule.getStartDate()) || request.getEndDate()
                .toLocalDate().isAfter(schedule.getEndDate())) {
            throw new ClassLogException(ClassLogErrorCode.INVALID_CLASS_LOG_DATE);
        }
    }
}
