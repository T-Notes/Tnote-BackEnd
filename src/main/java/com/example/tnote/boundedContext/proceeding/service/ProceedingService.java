package com.example.tnote.boundedContext.proceeding.service;

import com.example.tnote.base.exception.proceeding.ProceedingErrorResult;
import com.example.tnote.base.exception.proceeding.ProceedingException;
import com.example.tnote.base.exception.user.UserErrorResult;
import com.example.tnote.base.exception.user.UserException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.base.utils.FileUploadUtils;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDeleteResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingDetailResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingRequestDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingUpdateRequestDto;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.entity.ProceedingImage;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingImageRepository;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class ProceedingService {
    private final UserRepository userRepository;
    private final ProceedingRepository proceedingRepository;
    private final ProceedingImageRepository proceedingImageRepository;

    public ProceedingResponseDto save(Long userId, ProceedingRequestDto requestDto,
                                      List<MultipartFile> proceedingImages) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        Proceeding proceeding = requestDto.toEntity(user);
        if (proceedingImages != null && !proceedingImages.isEmpty()) {
            List<ProceedingImage> uploadedImages = uploadProceedingImages(proceeding, proceedingImages);
            proceeding.getProceedingImage().addAll(uploadedImages); // 이미지 리스트에 추가
        }
        return ProceedingResponseDto.of(proceedingRepository.save(proceeding));
    }

    @Transactional(readOnly = true)
    public List<ProceedingResponseDto> readAllProceeding(Long userId) {
        //todo slice 형태로 바꿔야합니다
        List<Proceeding> proceedings = proceedingRepository.findAllByUserId(userId);

        return proceedings.stream()
                .map(ProceedingResponseDto::of)
                .toList();
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

        return new ProceedingDetailResponseDto(proceeding, proceedingImages);
    }

    public ProceedingResponseDto updateProceeding(Long userId, Long proceedingId,
                                                  ProceedingUpdateRequestDto updateRequestDto,
                                                  List<MultipartFile> proceedingImages) {
        Proceeding proceeding = proceedingRepository.findByIdAndUserId(proceedingId, userId)
                .orElseThrow(() -> new ProceedingException(
                        ProceedingErrorResult.PROCEEDING_NOT_FOUNT));
        updateEachItem(updateRequestDto, proceeding, proceedingImages);

        return ProceedingResponseDto.of(proceeding);
    }

    private void updateEachItem(ProceedingUpdateRequestDto updateRequestDto, Proceeding proceeding,
                                List<MultipartFile> proceedingImages) {
        if (updateRequestDto.hasLocation()) {
            proceeding.updateLocation(updateRequestDto.getLocation());
        }
        if (updateRequestDto.hasWorkContents()) {
            proceeding.updateWorkContents(updateRequestDto.getWorkContents());
        }
        if (proceedingImages != null && !proceedingImages.isEmpty()) {
            proceeding.updateProceedingImage(deleteExistedImagesAndUploadNewImages(proceeding, proceedingImages));
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

    public List<ProceedingResponseDto> readDailyProceedings(Long userId, LocalDate date) {
        LocalDateTime startOfDay = DateUtils.getStartOfDay(date);
        LocalDateTime endOfDay = DateUtils.getEndOfDay(date);

        List<Proceeding> proceedings = proceedingRepository.findByUserIdAndStartDateBetween(userId, startOfDay,
                endOfDay);
        return proceedings.stream()
                .map(ProceedingResponseDto::of)
                .toList();
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
