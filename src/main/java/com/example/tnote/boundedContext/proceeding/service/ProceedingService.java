package com.example.tnote.boundedContext.proceeding.service;

import com.example.tnote.base.exception.proceeding.ProceedingErrorResult;
import com.example.tnote.base.exception.proceeding.ProceedingException;
import com.example.tnote.base.exception.schedule.ScheduleException;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.base.utils.FileUploadUtils;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDeleteResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDetailResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingRequestDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingSliceResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingUpdateRequestDto;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.entity.ProceedingImage;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingImageRepository;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingRepository;
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
public class ProceedingService {
    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final ProceedingRepository proceedingRepository;
    private final ProceedingImageRepository proceedingImageRepository;
    private final RecentLogService recentLogService;

    public ProceedingResponseDto save(Long userId, Long scheduleId, ProceedingRequestDto requestDto,
                                      List<MultipartFile> proceedingImages) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserException.USER_NOT_FOUND);
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> ScheduleException.SCHEDULE_NOT_FOUND);

        Proceeding proceeding = proceedingRepository.save(requestDto.toEntity(user, schedule));
        if (proceedingImages != null && !proceedingImages.isEmpty()) {
            List<ProceedingImage> uploadedImages = uploadProceedingImages(proceeding, proceedingImages);
            proceeding.getProceedingImage().addAll(uploadedImages);
        }
        recentLogService.saveRecentLog(userId, proceeding.getId(), "PROCEEDING");
        return ProceedingResponseDto.of(proceedingRepository.save(proceeding));
    }

    @Transactional(readOnly = true)
    public ProceedingSliceResponseDto readAllProceeding(Long userId, Long scheduleId, Pageable pageable) {
        List<Proceeding> proceedings = proceedingRepository.findAllByUserIdAndScheduleId(userId, scheduleId);
        Slice<Proceeding> allProceedingSlice = proceedingRepository.findAllByScheduleId(scheduleId, pageable);
        int numberOfProceeding = proceedings.size();
        List<ProceedingResponseDto> responseDto = allProceedingSlice.getContent().stream()
                .map(ProceedingResponseDto::of).toList();

        return ProceedingSliceResponseDto.builder()
                .proceedings(responseDto)
                .numberOfProceeding(numberOfProceeding)
                .page(allProceedingSlice.getPageable().getPageNumber())
                .isLast(allProceedingSlice.isLast())
                .build();
    }

    public ProceedingDeleteResponseDto deleteProceeding(Long userId, Long proceedingId) {
        Proceeding proceeding = proceedingRepository.findByIdAndUserId(proceedingId, userId)
                .orElseThrow(() -> new ProceedingException(
                        ProceedingErrorResult.PROCEEDING_NOT_FOUNT));
        proceedingRepository.delete(proceeding);

        return ProceedingDeleteResponseDto.builder()
                .id(proceeding.getId())
                .build();
    }

    public ProceedingDetailResponseDto getProceedingDetails(Long userId, Long proceedingId) {
        Proceeding proceeding = proceedingRepository.findByIdAndUserId(proceedingId, userId)
                .orElseThrow(() -> new ProceedingException(
                        ProceedingErrorResult.PROCEEDING_NOT_FOUNT));
        List<ProceedingImage> proceedingImages = proceedingImageRepository.findProceedingImageById(proceedingId);
        recentLogService.saveRecentLog(userId, proceeding.getId(), "PROCEEDING");

        return new ProceedingDetailResponseDto(proceeding, proceedingImages);
    }

    public ProceedingResponseDto updateProceeding(Long userId, Long proceedingId,
                                                  ProceedingUpdateRequestDto updateRequestDto,
                                                  List<MultipartFile> proceedingImages) {
        Proceeding proceeding = proceedingRepository.findByIdAndUserId(proceedingId, userId)
                .orElseThrow(() -> new ProceedingException(
                        ProceedingErrorResult.PROCEEDING_NOT_FOUNT));
        updateEachProceedingItem(updateRequestDto, proceeding, proceedingImages);
        recentLogService.saveRecentLog(userId, proceeding.getId(), "PROCEEDING");

        return ProceedingResponseDto.of(proceeding);
    }

    private void updateEachProceedingItem(ProceedingUpdateRequestDto requestDto, Proceeding proceeding,
                                          List<MultipartFile> proceedingImages) {
        updateProceedingFields(requestDto, proceeding);
        if (proceedingImages != null && !proceedingImages.isEmpty()) {
            List<ProceedingImage> updatedImages = deleteExistedImagesAndUploadNewImages(proceeding, proceedingImages);
            proceeding.updateProceedingImage(updatedImages);
        }
    }

    private void updateProceedingFields(ProceedingUpdateRequestDto requestDto, Proceeding proceeding) {
        if (requestDto.hasTitle()) {
            proceeding.updateTitle(requestDto.getTitle());
        }
        if (requestDto.hasStartDate()) {
            proceeding.updateStartDate(requestDto.getStartDate());
        }
        if (requestDto.hasEndDate()) {
            proceeding.updateEndDate(requestDto.getEndDate());
        }
        if (requestDto.hasLocation()) {
            proceeding.updateLocation(requestDto.getLocation());
        }
        if (requestDto.hasWorkContents()) {
            proceeding.updateWorkContents(requestDto.getWorkContents());
        }
    }

    private List<ProceedingImage> uploadProceedingImages(Proceeding proceeding, List<MultipartFile> proceedingImages) {
        return proceedingImages.stream()
                .map(file -> createProceedingImage(proceeding, file))
                .toList();
    }

    private ProceedingImage createProceedingImage(Proceeding proceeding, MultipartFile file) {
        String url;
        try {
            url = FileUploadUtils.saveFileAndGetUrl(file);
        } catch (IOException e) {
            log.error("File upload fail", e);
            throw new IllegalArgumentException();
        }

        log.info("url = {}", url);
        proceeding.clearProceedingImages();

        return proceedingImageRepository.save(ProceedingImage.builder()
                .proceedingImageUrl(url)
                .proceeding(proceeding)
                .build());
    }

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

        int numberOfProceeding = proceedings.size();
        List<ProceedingResponseDto> responseDto = allProceedingSlice.getContent().stream()
                .map(ProceedingResponseDto::of).toList();

        return ProceedingSliceResponseDto.builder()
                .proceedings(responseDto)
                .numberOfProceeding(numberOfProceeding)
                .page(allProceedingSlice.getPageable().getPageNumber())
                .isLast(allProceedingSlice.isLast())
                .build();
    }

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
        proceedingImageRepository.deleteByProceedingId(proceeding.getId());
    }
}
