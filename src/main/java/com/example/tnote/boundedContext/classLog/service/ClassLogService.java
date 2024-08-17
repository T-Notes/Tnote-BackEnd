package com.example.tnote.boundedContext.classLog.service;

import com.example.tnote.base.utils.AwsS3Uploader;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDetailResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSaveRequest;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponse;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSliceResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogUpdateRequestDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import com.example.tnote.boundedContext.classLog.exception.ClassLogErrorCode;
import com.example.tnote.boundedContext.classLog.exception.ClassLogException;
import com.example.tnote.boundedContext.classLog.repository.ClassLogImageRepository;
import com.example.tnote.boundedContext.classLog.repository.ClassLogRepository;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.exception.ScheduleErrorCode;
import com.example.tnote.boundedContext.schedule.exception.ScheduleException;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.exception.UserErrorCode;
import com.example.tnote.boundedContext.user.exception.UserException;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class ClassLogService {
    private final ClassLogRepository classLogRepository;
    private final ClassLogImageRepository classLogImageRepository;
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final RecentLogService recentLogService;
    private final AwsS3Uploader awsS3Uploader;

    public ClassLogResponse save(Long userId, Long scheduleId, ClassLogSaveRequest request,
                                 List<MultipartFile> classLogImages) {
        User user = findUserById(userId);
        Schedule schedule = findScheduleById(scheduleId);

        ClassLog classLog = classLogRepository.save(request.toEntity(user, schedule));
        if (classLog.getStartDate().toLocalDate().isBefore(schedule.getStartDate()) || classLog.getEndDate()
                .toLocalDate().isAfter(schedule.getEndDate())) {
            throw new ClassLogException(ClassLogErrorCode.INVALID_CLASS_LOG_DATE);
        }
        if (classLogImages != null && !classLogImages.isEmpty()) {
            List<ClassLogImage> uploadedImages = uploadClassLogImages(classLog, classLogImages);
            classLog.getClassLogImage().addAll(uploadedImages);
        }
        recentLogService.saveRecentLog(userId, classLog.getId(), scheduleId, "CLASS_LOG");
        return ClassLogResponse.of(classLog);
    }

    public ClassLogDeleteResponseDto deleteClassLog(Long userId, Long classLogId) {
        ClassLog classLog = findByIdAndUserId(classLogId, userId);

        deleteExistedImageByClassLog(classLog);
        classLogRepository.delete(classLog);
        recentLogService.deleteRecentLog(classLog.getId(), "CLASS_LOG");

        return ClassLogDeleteResponseDto.of(classLog);
    }

    public int deleteClassLogs(Long userId, List<Long> classLogIds) {
        classLogIds.forEach(classLogId -> {
            deleteClassLog(userId, classLogId);
        });
        return classLogIds.size();
    }

    @Transactional(readOnly = true)
    public ClassLogSliceResponseDto readAllClassLog(Long userId, Long scheduleId, Pageable pageable) {
        List<ClassLog> classLogList = classLogRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        Slice<ClassLog> allClassLogsSlice = classLogRepository.findAllByScheduleId(scheduleId, pageable);
        List<ClassLogResponse> classLogResponseDtos = allClassLogsSlice.getContent().stream()
                .map(ClassLogResponse::of).toList();

        return ClassLogSliceResponseDto.from(classLogResponseDtos, classLogList, allClassLogsSlice);
    }

    @Transactional(readOnly = true)
    public List<ClassLogResponse> findLogsByScheduleAndUser(Long scheduleId, Long userId) {
        List<ClassLog> logs = classLogRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        return logs.stream()
                .map(ClassLogResponse::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassLogResponse> findByTitle(String keyword, LocalDate startDate,
                                              LocalDate endDate, Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<ClassLog> logs = classLogRepository.findByTitleContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ClassLogResponse::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassLogResponse> findByContents(String keyword, LocalDate startDate,
                                                 LocalDate endDate, Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<ClassLog> logs = classLogRepository.findByContentsContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ClassLogResponse::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ClassLogResponse> findByTitleOrPlanOrContents(String keyword,
                                                              LocalDate startDate,
                                                              LocalDate endDate,
                                                              Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<ClassLog> logs = classLogRepository.findByTitleOrPlanOrClassContentsContaining(keyword,
                startOfDay, endOfDay, userId);

        return logs.stream()
                .map(ClassLogResponse::of)
                .toList();
    }

    public ClassLogDetailResponseDto getClassLogDetail(Long userId, Long classLogId) {
        ClassLog classLog = findByIdAndUserId(classLogId, userId);
        List<ClassLogImage> classLogImages = classLogImageRepository.findClassLogImagesByClassLogId(classLogId);
        recentLogService.saveRecentLog(userId, classLog.getId(), classLog.getSchedule().getId(), "CLASS_LOG");
        return new ClassLogDetailResponseDto(classLog, classLogImages);
    }

    public ClassLogResponse updateClassLog(Long userId, Long classLogId,
                                           ClassLogUpdateRequestDto classLogUpdateRequestDto,
                                           List<MultipartFile> classLogImages) {
        ClassLog classLog = findByIdAndUserId(classLogId, userId);
        updateEachClassLogItem(classLogUpdateRequestDto, classLog, classLogImages);
        recentLogService.saveRecentLog(userId, classLog.getId(), classLog.getSchedule().getId(), "CLASS_LOG");
        return ClassLogResponse.of(classLog);
    }

    private void updateEachClassLogItem(ClassLogUpdateRequestDto classLogUpdateRequestDto, ClassLog classLog,
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

    private void updateClassLogFields(ClassLogUpdateRequestDto classLogUpdateRequestDto, ClassLog classLog) {
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

    @Transactional(readOnly = true)
    public ClassLogSliceResponseDto readClassLogsByDate(Long userId, Long scheduleId, LocalDate startDate,
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
                .map(ClassLogResponse::of).toList();

        return ClassLogSliceResponseDto.from(classLogResponseDtos, classLogList, allClassLogsSlice);
    }

    @Transactional(readOnly = true)
    public List<ClassLogResponse> readDailyClassLog(Long userId, Long scheduleId, LocalDate date) {

        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<ClassLog> classLogs = classLogRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId, scheduleId,
                startOfDay, endOfDay);

        return classLogs.stream()
                .map(ClassLogResponse::of).toList();
    }

    @Transactional(readOnly = true)
    public List<ClassLogResponse> readMonthlyClassLog(Long userId, Long scheduleId, LocalDate date) {

        List<ClassLog> classLogs = classLogRepository.findByUserIdAndScheduleIdAndYearMonth(userId, scheduleId, date);

        return classLogs.stream()
                .map(ClassLogResponse::of).toList();
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

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private Schedule findScheduleById(Long scheduleId) {
        return scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleException(ScheduleErrorCode.SCHEDULE_NOT_FOUND));
    }

    private ClassLog findByIdAndUserId(Long classLogId, Long userId) {
        return classLogRepository.findByIdAndUserId(classLogId, userId)
                .orElseThrow(() -> new ClassLogException(ClassLogErrorCode.CLASS_LOG_NOT_FOUNT));
    }
}
