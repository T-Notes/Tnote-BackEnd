package com.example.tnote.boundedContext.classLog.service;

import com.example.tnote.base.exception.CustomException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.base.utils.FileUploadUtils;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDetailResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSliceResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogUpdateRequestDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import com.example.tnote.boundedContext.classLog.repository.ClassLogImageRepository;
import com.example.tnote.boundedContext.classLog.repository.ClassLogRepository;
import com.example.tnote.boundedContext.recentLog.service.RecentLogService;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.schedule.repository.ScheduleRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.io.IOException;
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

    public ClassLogResponseDto save(Long userId, Long scheduleId, ClassLogRequestDto request,
                                    List<MultipartFile> classLogImages) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> CustomException.USER_NOT_FOUND);
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> CustomException.SCHEDULE_NOT_FOUND);

        ClassLog classLog = classLogRepository.save(request.toEntity(user, schedule));

        if (classLogImages != null && !classLogImages.isEmpty()) {
            List<ClassLogImage> uploadedImages = uploadClassLogImages(classLog, classLogImages);
            classLog.getClassLogImage().addAll(uploadedImages);
        }
        recentLogService.saveRecentLog(userId, classLog.getId(), "CLASS_LOG");
        return ClassLogResponseDto.of(classLog);
    }

    public ClassLogDeleteResponseDto deleteClassLog(Long userId, Long classLogId) {
        ClassLog classLog = classLogRepository.findByIdAndUserId(classLogId, userId)
                .orElseThrow(() -> CustomException.CLASS_LOG_NOT_FOUNT);

        deleteExistedImages(classLog);
        classLogRepository.delete(classLog);

        return ClassLogDeleteResponseDto.builder()
                .id(classLog.getId())
                .build();
    }


    @Transactional(readOnly = true)
    public ClassLogSliceResponseDto readAllClassLog(Long userId, Long scheduleId, Pageable pageable) {
        List<ClassLog> classLogs = classLogRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        Slice<ClassLog> allClassLogsSlice = classLogRepository.findAllByScheduleId(scheduleId, pageable);
        int numberOfClassLog = classLogs.size();
        List<ClassLogResponseDto> classLogResponseDtos = allClassLogsSlice.getContent().stream()
                .map(ClassLogResponseDto::of).toList();

        return ClassLogSliceResponseDto.builder()
                .classLogs(classLogResponseDtos)
                .numberOfClassLog(numberOfClassLog)
                .page(allClassLogsSlice.getPageable().getPageNumber())
                .isLast(allClassLogsSlice.isLast())
                .build();
    }

    @Transactional(readOnly = true)
    public ClassLogDetailResponseDto getClassLogDetail(Long userId, Long classLogId) {
        ClassLog classLog = classLogRepository.findByIdAndUserId(classLogId, userId)
                .orElseThrow(() -> CustomException.CLASS_LOG_NOT_FOUNT);
        List<ClassLogImage> classLogImages = classLogImageRepository.findClassLogImagesByClassLogId(classLogId);
        recentLogService.saveRecentLog(userId, classLog.getId(), "CLASS_LOG");
        return new ClassLogDetailResponseDto(classLog, classLogImages);
    }

    public ClassLogResponseDto updateClassLog(Long userId, Long classLogId,
                                              ClassLogUpdateRequestDto classLogUpdateRequestDto,
                                              List<MultipartFile> classLogImages) {
        ClassLog classLog = classLogRepository.findByIdAndUserId(classLogId, userId)
                .orElseThrow(() -> CustomException.CLASS_LOG_NOT_FOUNT);
        updateEachClassLogItem(classLogUpdateRequestDto, classLog, classLogImages);
        recentLogService.saveRecentLog(userId, classLog.getId(), "CLASS_LOG");
        return ClassLogResponseDto.of(classLog);
    }

    private void updateEachClassLogItem(ClassLogUpdateRequestDto classLogUpdateRequestDto, ClassLog classLog,
                                        List<MultipartFile> classLogImages) {
        updateClassLogFields(classLogUpdateRequestDto, classLog);
        if (classLogImages != null && !classLogImages.isEmpty()) {
            List<ClassLogImage> updatedImages = deleteExistedImagesAndUploadNewImages(classLog, classLogImages);
            classLog.updateClassLogImages(updatedImages);
        }
    }

    private void updateClassLogFields(ClassLogUpdateRequestDto classLogUpdateRequestDto, ClassLog classLog) {
        if (classLogUpdateRequestDto.hasTitle()) {
            classLog.updateTitle(classLogUpdateRequestDto.getTitle());
        }
        if (classLogUpdateRequestDto.hasStartDate()) {
            classLog.updateStartDate(classLogUpdateRequestDto.getStartDate());
        }
        if (classLogUpdateRequestDto.hasEndDate()) {
            classLog.updateEndDate(classLogUpdateRequestDto.getEndDate());
        }
        if (classLogUpdateRequestDto.hasPlan()) {
            classLog.updatePlan(classLogUpdateRequestDto.getPlan());
        }
        if (classLogUpdateRequestDto.hasSubmission()) {
            classLog.updateSubmission(classLogUpdateRequestDto.getSubmission());
        }
        if (classLogUpdateRequestDto.hasClassContents()) {
            classLog.updateClassContents(classLogUpdateRequestDto.getClassContents());
        }
        if (classLogUpdateRequestDto.hasMagnitude()) {
            classLog.updateMagnitude(classLogUpdateRequestDto.getMagnitude());
        }
    }

    private List<ClassLogImage> uploadClassLogImages(ClassLog classLog, List<MultipartFile> classLogImages) {
        return classLogImages.stream()
                .map(file -> createClassLogImage(classLog, file))
                .toList();
    }

    private ClassLogImage createClassLogImage(ClassLog classLog, MultipartFile file) {
        String url;
        try {
            url = FileUploadUtils.saveFileAndGetUrl(file);
        } catch (IOException e) {
            log.error("File upload fail", e);
            throw new IllegalArgumentException();
        }

        log.info("url = {}", url);
        classLog.clearClassLogImages();

        return classLogImageRepository.save(ClassLogImage.builder()
                .classLogImageUrl(url)
                .classLog(classLog)
                .build());
    }

    public ClassLogSliceResponseDto readClassLogsByDate(Long userId, Long scheduleId, LocalDate startDate,
                                                        LocalDate endDate, Pageable pageable) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<ClassLog> classLogs = classLogRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId, scheduleId,
                startOfDay, endOfDay);
        Slice<ClassLog> allClassLogsSlice = classLogRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(
                userId, scheduleId,
                startOfDay, endOfDay, pageable);

        int numberOfClassLog = classLogs.size();
        List<ClassLogResponseDto> classLogResponseDtos = allClassLogsSlice.getContent().stream()
                .map(ClassLogResponseDto::of).toList();

        return ClassLogSliceResponseDto.builder()
                .classLogs(classLogResponseDtos)
                .numberOfClassLog(numberOfClassLog)
                .page(allClassLogsSlice.getPageable().getPageNumber())
                .isLast(allClassLogsSlice.isLast())
                .build();
    }

    public List<ClassLogResponseDto> readDailyClassLog(Long userId, Long scheduleId, LocalDate date) {

        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<ClassLog> classLogs = classLogRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId, scheduleId,
                startOfDay, endOfDay);

        return classLogs.stream()
                .map(ClassLogResponseDto::of).toList();
    }

    public List<ClassLogResponseDto> readMonthlyClassLog(Long userId, Long scheduleId, LocalDate date) {

        List<ClassLog> classLogs = classLogRepository.findByUserIdAndScheduleIdAndYearMonth(userId, scheduleId, date);

        return classLogs.stream()
                .map(ClassLogResponseDto::of).toList();
    }

    private List<ClassLogImage> deleteExistedImagesAndUploadNewImages(ClassLog classLog,
                                                                      List<MultipartFile> classLogImages) {
        deleteExistedImages(classLog);
        return uploadClassLogImages(classLog, classLogImages);
    }

    private void deleteExistedImages(ClassLog classLog) {
        classLogImageRepository.deleteByClassLogId(classLog.getId());
    }

}
