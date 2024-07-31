package com.example.tnote.boundedContext.proceeding.service;

import com.example.tnote.base.exception.CustomExceptions;
import com.example.tnote.base.exception.ErrorCodes;
import com.example.tnote.base.utils.AwsS3Uploader;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDeleteResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDetailResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingRequestDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingSliceResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingUpdateRequestDto;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.entity.ProceedingImage;
import com.example.tnote.boundedContext.proceeding.exception.ProceedingErrorCode;
import com.example.tnote.boundedContext.proceeding.exception.ProceedingException;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingImageRepository;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingRepository;
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
public class ProceedingService {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ProceedingRepository proceedingRepository;
    private final ProceedingImageRepository proceedingImageRepository;
    private final RecentLogService recentLogService;
    private final AwsS3Uploader awsS3Uploader;

    public ProceedingResponseDto save(Long userId, Long scheduleId, ProceedingRequestDto requestDto,
                                      List<MultipartFile> proceedingImages) {
        User user = findUserById(userId);
        Schedule schedule = findScheduleById(scheduleId);

        Proceeding proceeding = proceedingRepository.save(requestDto.toEntity(user, schedule));

        if (proceeding.getStartDate().toLocalDate().isBefore(schedule.getStartDate()) || proceeding.getEndDate()
                .toLocalDate().isAfter(schedule.getEndDate())) {
            throw new ProceedingException(ProceedingErrorCode.INVALID_PROCEEDING_DATE);
        }
        if (proceedingImages != null && !proceedingImages.isEmpty()) {
            List<ProceedingImage> uploadedImages = uploadProceedingImages(proceeding, proceedingImages);
            proceeding.getProceedingImage().addAll(uploadedImages);
        }
        recentLogService.saveRecentLog(userId, proceeding.getId(), scheduleId, "PROCEEDING");
        return ProceedingResponseDto.of(proceeding);
    }

    @Transactional(readOnly = true)
    public ProceedingSliceResponseDto readAllProceeding(Long userId, Long scheduleId, Pageable pageable) {
        List<Proceeding> proceedings = proceedingRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        Slice<Proceeding> allProceedingSlice = proceedingRepository.findAllByScheduleId(scheduleId, pageable);
        List<ProceedingResponseDto> responseDto = allProceedingSlice.getContent().stream()
                .map(ProceedingResponseDto::of).toList();

        return ProceedingSliceResponseDto.from(responseDto, proceedings, allProceedingSlice);
    }

    public ProceedingDeleteResponseDto deleteProceeding(Long userId, Long proceedingId) {
        Proceeding proceeding = findByIdAndUserId(proceedingId, userId);

        deleteExistedImagesByProceeding(proceeding);
        proceedingRepository.delete(proceeding);
        recentLogService.deleteRecentLog(proceeding.getId(), "PROCEEDING");

        return ProceedingDeleteResponseDto.of(proceeding);
    }

    public int deleteProceedings(Long userId, List<Long> proceedingIds) {
        proceedingIds.forEach(proceedingId -> {
            deleteProceeding(userId, proceedingId);
        });
        return proceedingIds.size();
    }

    public ProceedingDetailResponseDto getProceedingDetail(Long userId, Long proceedingId) {
        Proceeding proceeding = findByIdAndUserId(proceedingId, userId);
        List<ProceedingImage> proceedingImages = proceedingImageRepository.findProceedingImageByProceedingId(
                proceedingId);
        recentLogService.saveRecentLog(userId, proceeding.getId(), proceeding.getSchedule().getId(), "PROCEEDING");

        return new ProceedingDetailResponseDto(proceeding, proceedingImages);
    }

    public ProceedingResponseDto updateProceeding(Long userId, Long proceedingId,
                                                  ProceedingUpdateRequestDto updateRequestDto,
                                                  List<MultipartFile> proceedingImages) {
        Proceeding proceeding = findByIdAndUserId(proceedingId, userId);
        updateEachProceedingItem(updateRequestDto, proceeding, proceedingImages);
        recentLogService.saveRecentLog(userId, proceeding.getId(), proceeding.getSchedule().getId(), "PROCEEDING");

        return ProceedingResponseDto.of(proceeding);
    }

    @Transactional(readOnly = true)
    public List<ProceedingResponseDto> findLogsByScheduleAndUser(Long scheduleId, Long userId) {
        List<Proceeding> logs = proceedingRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        return logs.stream()
                .map(ProceedingResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProceedingResponseDto> findByTitle(String keyword, LocalDate startDate,
                                                   LocalDate endDate, Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Proceeding> logs = proceedingRepository.findByTitleContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ProceedingResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProceedingResponseDto> findByContents(String keyword, LocalDate startDate,
                                                      LocalDate endDate, Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Proceeding> logs = proceedingRepository.findByContentsContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ProceedingResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProceedingResponseDto> findByTitleOrPlanOrContents(String keyword,
                                                                   LocalDate startDate,
                                                                   LocalDate endDate,
                                                                   Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Proceeding> logs = proceedingRepository.findByTitleOrPlanOrClassContentsContaining(keyword,
                startOfDay, endOfDay, userId);

        return logs.stream()
                .map(ProceedingResponseDto::of)
                .toList();
    }

    private void updateEachProceedingItem(ProceedingUpdateRequestDto requestDto, Proceeding proceeding,
                                          List<MultipartFile> proceedingImages) {
        updateProceedingFields(requestDto, proceeding);
        if (proceedingImages == null || proceedingImages.isEmpty()) {
            deleteExistedImages(proceeding);
        }
        if (proceedingImages != null && !proceedingImages.isEmpty()) {
            List<ProceedingImage> updatedImages = deleteExistedImagesAndUploadNewImages(proceeding, proceedingImages);
            proceeding.updateProceedingImage(updatedImages);
        }
    }

    private void updateProceedingFields(ProceedingUpdateRequestDto requestDto, Proceeding proceeding) {
        proceeding.updateTitle(requestDto.getTitle());
        proceeding.updateStartDate(requestDto.getStartDate());
        proceeding.updateEndDate(requestDto.getEndDate());
        proceeding.updateLocation(requestDto.getLocation());
        proceeding.updateWorkContents(requestDto.getWorkContents());
    }

    private List<ProceedingImage> uploadProceedingImages(Proceeding proceeding, List<MultipartFile> proceedingImages) {
        return proceedingImages.stream()
                .map(file -> awsS3Uploader.upload(file, "proceeding"))
                .map(pair -> createProceedingImage(proceeding, pair.getFirst(), pair.getSecond()))
                .toList();
    }


    private ProceedingImage createProceedingImage(Proceeding proceeding, String url, String originalFileName) {
        log.info("url = {}", url);
        proceeding.clearProceedingImages();

        return proceedingImageRepository.save(ProceedingImage.builder()
                .proceedingImageUrl(url)
                .proceeding(proceeding)
                .originalFileName(originalFileName)
                .build());
    }

    @Transactional(readOnly = true)
    public ProceedingSliceResponseDto readProceedingsByDate(Long userId, Long scheduleId, LocalDate startDate,
                                                            LocalDate endDate, Pageable pageable) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<Proceeding> proceedings = proceedingRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);
        Slice<Proceeding> allProceedingSlice = proceedingRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(
                userId, scheduleId, startOfDay,
                endOfDay, pageable);

        List<ProceedingResponseDto> responseDto = allProceedingSlice.getContent().stream()
                .map(ProceedingResponseDto::of).toList();

        return ProceedingSliceResponseDto.from(responseDto, proceedings, allProceedingSlice);
    }

    @Transactional(readOnly = true)
    public List<ProceedingResponseDto> readDailyProceedings(Long userId, Long scheduleId, LocalDate date) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<Proceeding> proceedings = proceedingRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);

        return proceedings.stream()
                .map(ProceedingResponseDto::of).toList();
    }

    public List<ProceedingResponseDto> readMonthlyProceedings(Long userId, Long scheduleId, LocalDate date) {
        List<Proceeding> proceedings = proceedingRepository.findByUserIdAndScheduleIdAndYearMonth(userId,
                scheduleId, date);

        return proceedings.stream()
                .map(ProceedingResponseDto::of).toList();
    }

    private List<ProceedingImage> deleteExistedImagesAndUploadNewImages(Proceeding proceeding,
                                                                        List<MultipartFile> proceedingImages) {
        deleteExistedImages(proceeding);
        return uploadProceedingImages(proceeding, proceedingImages);
    }

    private void deleteExistedImages(Proceeding proceeding) {
        deleteS3Images(proceeding);
        proceedingImageRepository.deleteByProceedingId(proceeding.getId());
    }

    private void deleteExistedImagesByProceeding(Proceeding proceeding) {
        deleteS3Images(proceeding);
    }

    private void deleteS3Images(Proceeding proceeding) {
        List<ProceedingImage> proceedingImages = proceeding.getProceedingImage();
        for (ProceedingImage proceedingImage : proceedingImages) {
            String imageKey = proceedingImage.getProceedingImageUrl().substring(49);
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

    private Proceeding findByIdAndUserId(Long proceedingId, Long userId) {
        return proceedingRepository.findByIdAndUserId(proceedingId, userId)
                .orElseThrow(() -> new ProceedingException(ProceedingErrorCode.PROCEEDING_NOT_FOUNT));
    }
}
