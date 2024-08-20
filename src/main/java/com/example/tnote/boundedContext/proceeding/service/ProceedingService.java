package com.example.tnote.boundedContext.proceeding.service;

import com.example.tnote.base.utils.AwsS3Uploader;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDeleteResponse;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingSaveRequest;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponse;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponses;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingUpdateRequest;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.entity.ProceedingImage;
import com.example.tnote.boundedContext.proceeding.exception.ProceedingErrorCode;
import com.example.tnote.boundedContext.proceeding.exception.ProceedingException;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingImageRepository;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingRepository;
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
public class ProceedingService {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ProceedingRepository proceedingRepository;
    private final ProceedingImageRepository proceedingImageRepository;
    private final RecentLogService recentLogService;
    private final AwsS3Uploader awsS3Uploader;

    public ProceedingService(final UserRepository userRepository, final ScheduleRepository scheduleRepository,
                             final ProceedingRepository proceedingRepository,
                             final ProceedingImageRepository proceedingImageRepository,
                             final RecentLogService recentLogService, final AwsS3Uploader awsS3Uploader) {
        this.userRepository = userRepository;
        this.scheduleRepository = scheduleRepository;
        this.proceedingRepository = proceedingRepository;
        this.proceedingImageRepository = proceedingImageRepository;
        this.recentLogService = recentLogService;
        this.awsS3Uploader = awsS3Uploader;
    }

    @Transactional
    public ProceedingResponse save(final Long userId, final Long scheduleId, final ProceedingSaveRequest request,
                                   final List<MultipartFile> proceedingImages) {
        User user = userRepository.findUserById(userId);
        Schedule schedule = scheduleRepository.findScheduleById(scheduleId);

        validateIncorrectTime(request, schedule);
        Proceeding proceeding = proceedingRepository.save(request.toEntity(user, schedule));

        if (proceedingImages != null && !proceedingImages.isEmpty()) {
            List<ProceedingImage> uploadedImages = uploadProceedingImages(proceeding, proceedingImages);
            proceeding.getProceedingImage().addAll(uploadedImages);
        }
        recentLogService.saveRecentLog(userId, proceeding.getId(), scheduleId, "PROCEEDING");
        return ProceedingResponse.from(proceeding);
    }

    public ProceedingResponses findAll(final Long userId, final Long scheduleId, final Pageable pageable) {
        List<Proceeding> proceedings = proceedingRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        Slice<Proceeding> allProceedingSlice = proceedingRepository.findAllByScheduleId(scheduleId, pageable);
        List<ProceedingResponse> responseDto = allProceedingSlice.getContent().stream()
                .map(ProceedingResponse::from).toList();

        return ProceedingResponses.of(responseDto, proceedings, allProceedingSlice);
    }

    @Transactional
    public ProceedingDeleteResponse delete(final Long userId, final Long proceedingId) {
        Proceeding proceeding = findByIdAndUserId(proceedingId, userId);

        deleteExistedImagesByProceeding(proceeding);
        proceedingRepository.delete(proceeding);
        recentLogService.deleteRecentLog(proceeding.getId(), "PROCEEDING");

        return ProceedingDeleteResponse.from(proceeding);
    }

    public int deleteProceedings(final Long userId, final List<Long> proceedingIds) {
        proceedingIds.forEach(proceedingId -> {
            delete(userId, proceedingId);
        });
        return proceedingIds.size();
    }

    @Transactional
    public ProceedingResponse find(final Long userId, final Long proceedingId) {
        Proceeding proceeding = findByIdAndUserId(proceedingId, userId);
        recentLogService.saveRecentLog(userId, proceeding.getId(), proceeding.getSchedule().getId(), "PROCEEDING");

        return ProceedingResponse.from(proceeding);
    }

    @Transactional
    public ProceedingResponse update(final Long userId, final Long proceedingId,
                                     final ProceedingUpdateRequest request,
                                     final List<MultipartFile> proceedingImages) {
        Proceeding proceeding = findByIdAndUserId(proceedingId, userId);
        updateEachProceedingItem(request, proceeding, proceedingImages);
        recentLogService.saveRecentLog(userId, proceeding.getId(), proceeding.getSchedule().getId(), "PROCEEDING");

        return ProceedingResponse.from(proceeding);
    }

    public List<ProceedingResponse> findLogsByScheduleAndUser(final Long scheduleId, final Long userId) {
        List<Proceeding> logs = proceedingRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        return logs.stream()
                .map(ProceedingResponse::from)
                .toList();
    }

    public List<ProceedingResponse> findByTitle(final String keyword, final LocalDate startDate,
                                                final LocalDate endDate, final Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Proceeding> logs = proceedingRepository.findByTitleContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ProceedingResponse::from)
                .toList();
    }

    public List<ProceedingResponse> findByContents(final String keyword, final LocalDate startDate,
                                                   final LocalDate endDate, final Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Proceeding> logs = proceedingRepository.findByContentsContaining(keyword, startOfDay, endOfDay,
                userId);
        return logs.stream()
                .map(ProceedingResponse::from)
                .toList();
    }

    public List<ProceedingResponse> findByTitleOrPlanOrContents(final String keyword,
                                                                final LocalDate startDate,
                                                                final LocalDate endDate,
                                                                final Long userId) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);
        List<Proceeding> logs = proceedingRepository.findByTitleOrPlanOrClassContentsContaining(keyword,
                startOfDay, endOfDay, userId);

        return logs.stream()
                .map(ProceedingResponse::from)
                .toList();
    }

    private void updateEachProceedingItem(final ProceedingUpdateRequest requestDto, final Proceeding proceeding,
                                          final List<MultipartFile> proceedingImages) {
        updateProceedingFields(requestDto, proceeding);
        if (proceedingImages == null || proceedingImages.isEmpty()) {
            deleteExistedImages(proceeding);
        }
        if (proceedingImages != null && !proceedingImages.isEmpty()) {
            List<ProceedingImage> updatedImages = deleteExistedImagesAndUploadNewImages(proceeding, proceedingImages);
            proceeding.updateProceedingImage(updatedImages);
        }
    }

    private void updateProceedingFields(final ProceedingUpdateRequest requestDto, final Proceeding proceeding) {
        proceeding.updateTitle(requestDto.getTitle());
        proceeding.updateStartDate(requestDto.getStartDate());
        proceeding.updateEndDate(requestDto.getEndDate());
        proceeding.updateLocation(requestDto.getLocation());
        proceeding.updateWorkContents(requestDto.getWorkContents());
    }

    private List<ProceedingImage> uploadProceedingImages(final Proceeding proceeding, final List<MultipartFile> proceedingImages) {
        return proceedingImages.stream()
                .map(file -> awsS3Uploader.upload(file, "proceeding"))
                .map(pair -> createProceedingImage(proceeding, pair.getFirst(), pair.getSecond()))
                .toList();
    }


    private ProceedingImage createProceedingImage(final Proceeding proceeding, final String url, final String originalFileName) {
        log.info("url = {}", url);
        proceeding.clearProceedingImages();

        return proceedingImageRepository.save(ProceedingImage.builder()
                .proceedingImageUrl(url)
                .proceeding(proceeding)
                .originalFileName(originalFileName)
                .build());
    }

    @Transactional(readOnly = true)
    public ProceedingResponses readProceedingsByDate(Long userId, Long scheduleId, LocalDate startDate,
                                                     LocalDate endDate, Pageable pageable) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(startDate);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(endDate);

        List<Proceeding> proceedings = proceedingRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);
        Slice<Proceeding> allProceedingSlice = proceedingRepository.findAllByUserIdAndScheduleIdAndCreatedAtBetween(
                userId, scheduleId, startOfDay,
                endOfDay, pageable);

        List<ProceedingResponse> responseDto = allProceedingSlice.getContent().stream()
                .map(ProceedingResponse::from).toList();

        return ProceedingResponses.of(responseDto, proceedings, allProceedingSlice);
    }

    @Transactional(readOnly = true)
    public List<ProceedingResponse> readDailyProceedings(Long userId, Long scheduleId, LocalDate date) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<Proceeding> proceedings = proceedingRepository.findByUserIdAndScheduleIdAndStartDateBetween(userId,
                scheduleId, startOfDay,
                endOfDay);

        return proceedings.stream()
                .map(ProceedingResponse::from).toList();
    }

    public List<ProceedingResponse> readMonthlyProceedings(Long userId, Long scheduleId, LocalDate date) {
        List<Proceeding> proceedings = proceedingRepository.findByUserIdAndScheduleIdAndYearMonth(userId,
                scheduleId, date);

        return proceedings.stream()
                .map(ProceedingResponse::from).toList();
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

    private Proceeding findByIdAndUserId(Long proceedingId, Long userId) {
        return proceedingRepository.findByIdAndUserId(proceedingId, userId)
                .orElseThrow(() -> new ProceedingException(ProceedingErrorCode.PROCEEDING_NOT_FOUNT));
    }

    private void validateIncorrectTime(final ProceedingSaveRequest request, Schedule schedule) {
        if (request.getStartDate().toLocalDate().isBefore(schedule.getStartDate()) || request.getEndDate()
                .toLocalDate().isAfter(schedule.getEndDate())) {
            throw new ProceedingException(ProceedingErrorCode.INVALID_PROCEEDING_DATE);
        }
    }
}
