package com.example.tnote.boundedContext.classLog.service;

import com.example.tnote.base.utils.AwsS3Uploader;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponse;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDetailResponseDto;
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

    public ClassLogService(final ClassLogRepository classLogRepository, final ClassLogImageRepository classLogImageRepository,
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

        if (request.getStartDate().toLocalDate().isBefore(schedule.getStartDate()) || request.getEndDate()
                .toLocalDate().isAfter(schedule.getEndDate())) {
            throw new ClassLogException(ClassLogErrorCode.INVALID_CLASS_LOG_DATE);
        }

        ClassLog classLog = classLogRepository.save(request.toEntity(user, schedule));
        if (classLogImages != null && !classLogImages.isEmpty()) {
            List<ClassLogImage> uploadedImages = uploadClassLogImages(classLog, classLogImages);
            classLog.getClassLogImage().addAll(uploadedImages);
        }
        recentLogService.saveRecentLog(userId, classLog.getId(), scheduleId, "CLASS_LOG");
        return ClassLogResponse.from(classLog);
    }

    @Transactional
    public ClassLogDeleteResponse delete(final Long userId, final Long classLogId) {
        ClassLog classLog = findByIdAndUserId(classLogId, userId);

        deleteExistedImageByClassLog(classLog);
        classLogRepository.delete(classLog);
        recentLogService.deleteRecentLog(classLog.getId(), "CLASS_LOG");

        return ClassLogDeleteResponse.from(classLog);
    }

    @Transactional
    public int deleteClassLogs(final Long userId, final List<Long> classLogIds) {
        classLogIds.forEach(classLogId -> {
            delete(userId, classLogId);
        });
        return classLogIds.size();
    }

    public ClassLogResponses findAll(final Long userId, final Long scheduleId, final Pageable pageable) {
        List<ClassLog> classLogList = classLogRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        Slice<ClassLog> allClassLogsSlice = classLogRepository.findAllByScheduleId(scheduleId, pageable);
        List<ClassLogResponse> classLogResponseDtos = allClassLogsSlice.getContent().stream()
                .map(ClassLogResponse::from).toList();

        return ClassLogResponses.of(classLogResponseDtos, classLogList, allClassLogsSlice);
    }

    public List<ClassLogResponse> findLogsByScheduleAndUser(Long scheduleId, Long userId) {
        List<ClassLog> logs = classLogRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        return logs.stream()
                .map(ClassLogResponse::from)
                .toList();
    }

    public List<ClassLogResponse> findByTitle(String keyword, LocalDate startDate,
                                              LocalDate endDate, Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<ClassLog> logs = classLogRepository.findByTitleContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ClassLogResponse::from)
                .toList();
    }

    public List<ClassLogResponse> findByContents(String keyword, LocalDate startDate,
                                                 LocalDate endDate, Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<ClassLog> logs = classLogRepository.findByContentsContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ClassLogResponse::from)
                .toList();
    }

    public List<ClassLogResponse> findByTitleOrPlanOrContents(String keyword,
                                                              LocalDate startDate,
                                                              LocalDate endDate,
                                                              Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<ClassLog> logs = classLogRepository.findByTitleOrPlanOrClassContentsContaining(keyword,
                startOfDay, endOfDay, userId);

        return logs.stream()
                .map(ClassLogResponse::from)
                .toList();
    }

    @Transactional
    public ClassLogDetailResponseDto getClassLogDetail(Long userId, Long classLogId) {
        ClassLog classLog = findByIdAndUserId(classLogId, userId);
        List<ClassLogImage> classLogImages = classLogImageRepository.findClassLogImagesByClassLogId(classLogId);
        recentLogService.saveRecentLog(userId, classLog.getId(), classLog.getSchedule().getId(), "CLASS_LOG");
        return new ClassLogDetailResponseDto(classLog, classLogImages);
    }

    @Transactional
    public ClassLogResponse updateClassLog(Long userId, Long classLogId,
                                           ClassLogUpdateRequest classLogUpdateRequestDto,
                                           List<MultipartFile> classLogImages) {
        ClassLog classLog = findByIdAndUserId(classLogId, userId);
        updateEachClassLogItem(classLogUpdateRequestDto, classLog, classLogImages);
        recentLogService.saveRecentLog(userId, classLog.getId(), classLog.getSchedule().getId(), "CLASS_LOG");
        return ClassLogResponse.from(classLog);
    }

    private void updateEachClassLogItem(ClassLogUpdateRequest classLogUpdateRequestDto, ClassLog classLog,
                                        List<MultipartFile> classLogImages) {
        updateClassLogFields(classLogUpdateRequestDto, classLog);
        if (classLogImages == null || classLogImages.isEmpty()) {
            deleteExistedImages(classLog);
        }
        if (classLogImages != null && !classLogImages.isEmpty()) {
            List<ClassLogImage> updatedImages = deleteExistedImagesAndUploadNewImages(classLog, classLogImages);
            classLog.updateClassLogImages(updatedImages);
        }
    }

    private void updateClassLogFields(ClassLogUpdateRequest classLogUpdateRequestDto, ClassLog classLog) {
        classLog.updateTitle(classLogUpdateRequestDto.getTitle());
        classLog.updateStartDate(classLogUpdateRequestDto.getStartDate());
        classLog.updateEndDate(classLogUpdateRequestDto.getEndDate());
        classLog.updatePlan(classLogUpdateRequestDto.getPlan());
        classLog.updateSubmission(classLogUpdateRequestDto.getSubmission());
        classLog.updateClassContents(classLogUpdateRequestDto.getClassContents());
        classLog.updateMagnitude(classLogUpdateRequestDto.getMagnitude());
    }

    private List<ClassLogImage> uploadClassLogImages(ClassLog classLog, List<MultipartFile> classLogImages) {
        return classLogImages.stream()
                .map(file -> awsS3Uploader.upload(file, "classLog"))
                .map(pair -> createClassLogImage(classLog, pair.getFirst(), pair.getSecond()))
                .toList();
    }

    private ClassLogImage createClassLogImage(ClassLog classLog, String imageUrl, String originalFileName) {
        log.info("url = {}", imageUrl);
        classLog.clearClassLogImages();

        return classLogImageRepository.save(ClassLogImage.builder()
                .originalFileName(originalFileName)
                .classLogImageUrl(imageUrl)
                .classLog(classLog)
                .build());
    }

    public ClassLogResponses readClassLogsByDate(Long userId, Long scheduleId, LocalDate startDate,
                                                 LocalDate endDate, Pageable pageable) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<ClassLog> classLogList = classLogRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId,
                startOfDay, endOfDay);
        Slice<ClassLog> allClassLogsSlice = classLogRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(
                userId, scheduleId,
                startOfDay, endOfDay, pageable);

        List<ClassLogResponse> classLogResponseDtos = allClassLogsSlice.getContent().stream()
                .map(ClassLogResponse::from).toList();

        return ClassLogResponses.of(classLogResponseDtos, classLogList, allClassLogsSlice);
    }

    public List<ClassLogResponse> readDailyClassLog(Long userId, Long scheduleId, LocalDate date) {

        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<ClassLog> classLogs = classLogRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId, scheduleId,
                startOfDay, endOfDay);

        return classLogs.stream()
                .map(ClassLogResponse::from).toList();
    }

    public List<ClassLogResponse> readMonthlyClassLog(Long userId, Long scheduleId, LocalDate date) {

        List<ClassLog> classLogs = classLogRepository.findByUserIdAndScheduleIdAndYearMonth(userId, scheduleId, date);

        return classLogs.stream()
                .map(ClassLogResponse::from).toList();
    }


    private List<ClassLogImage> deleteExistedImagesAndUploadNewImages(ClassLog classLog,
                                                                      List<MultipartFile> classLogImages) {
        deleteExistedImages(classLog);
        return uploadClassLogImages(classLog, classLogImages);
    }

    private void deleteExistedImages(ClassLog classLog) {
        System.out.println("Deleting existing images for classLog ID: " + classLog.getId());
        deleteS3Images(classLog);
        classLogImageRepository.deleteByClassLogId(classLog.getId());
    }

    private void deleteExistedImageByClassLog(ClassLog classLog) {
        System.out.println("Deleting existing images for classLog ID: " + classLog.getId());
        deleteS3Images(classLog);
    }

    private void deleteS3Images(ClassLog classLog) {
        System.out.println("Starting to delete images from S3 for classLog ID: " + classLog.getId());
        List<ClassLogImage> classLogImages = classLog.getClassLogImage();
        for (ClassLogImage classLogImage : classLogImages) {
            String imageKey = classLogImage.getClassLogImageUrl().substring(49);
            System.out.println("Deleting image from S3: " + imageKey);
            awsS3Uploader.deleteImage(imageKey);
        }
    }

    private ClassLog findByIdAndUserId(Long classLogId, Long userId) {
        return classLogRepository.findByIdAndUserId(classLogId, userId)
                .orElseThrow(() -> new ClassLogException(ClassLogErrorCode.CLASS_LOG_NOT_FOUNT));
    }
}
