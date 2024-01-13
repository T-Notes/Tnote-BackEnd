package com.example.tnote.boundedContext.proceeding.service;

import com.example.tnote.base.exception.CommonErrorResult;
import com.example.tnote.base.exception.CommonException;
import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.base.utils.FileUploadUtils;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
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

    public ProceedingResponseDto save(Long userId, ProceedingRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        LocalDateTime startDate = DateUtils.adjustStartDateTime(requestDto.getStartDate(), requestDto.isAllDay());
        LocalDateTime endDate = DateUtils.adjustEndDateTime(requestDto.getEndDate(), requestDto.isAllDay());

        Proceeding proceeding = Proceeding.builder()
                .user(user)
                .title(requestDto.getTitle())
                .startDate(startDate)
                .endDate(endDate)
                .location(requestDto.getLocation())
                .workContents(requestDto.getWorkContents())
                .build();
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
        Proceeding proceeding = proceedingRepository.findByIdAndUserId(proceedingId, userId).orElseThrow();
        proceedingRepository.delete(proceeding);

        return ProceedingDeleteResponseDto.builder()
                .id(proceeding.getId())
                .build();
    }

    public ProceedingDetailResponseDto getProceedingDetails(Long userId, Long proceedingId) {
        Proceeding proceeding = proceedingRepository.findByIdAndUserId(proceedingId, userId).orElseThrow();

        return new ProceedingDetailResponseDto(proceeding);
    }

    public ProceedingResponseDto updateProceeding(Long userId, Long proceedingId,
                                                  ProceedingUpdateRequestDto updateRequestDto) {
        Proceeding proceeding = proceedingRepository.findByIdAndUserId(proceedingId, userId).orElseThrow();
        updateEachItem(updateRequestDto, proceeding);

        return ProceedingResponseDto.of(proceeding);
    }

    private void updateEachItem(ProceedingUpdateRequestDto updateRequestDto, Proceeding proceeding) {
        if (updateRequestDto.hasLocation()) {
            proceeding.updateLocation(updateRequestDto.getLocation());
        }
        if (updateRequestDto.hasWorkContents()) {
            proceeding.updateWorkContents(updateRequestDto.getWorkContents());
        }
    }

    private List<ProceedingImage> uploadProceedingImages(ProceedingRequestDto requestDto, Proceeding proceeding) {
        return requestDto.getProceedingImages().stream()
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
}
