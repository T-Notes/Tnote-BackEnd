package com.example.tnote.boundedContext.classLog.service;

import com.example.tnote.base.exception.CommonErrorResult;
import com.example.tnote.base.exception.CommonException;
import com.example.tnote.base.exception.UserErrorResult;
import com.example.tnote.base.exception.UserException;
import com.example.tnote.base.utils.DateUtils;
import com.example.tnote.base.utils.FileUploadUtils;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDetailResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogUpdateRequestDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import com.example.tnote.boundedContext.classLog.repository.ClassLogImageRepository;
import com.example.tnote.boundedContext.classLog.repository.ClassLogRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
public class ClassLogService {
    private final ClassLogRepository classLogRepository;
    private final ClassLogImageRepository classLogImageRepository;
    private final UserRepository userRepository;

    public ClassLogResponseDto save(Long userId, ClassLogRequestDto request, List<MultipartFile> classLogImages) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorResult.USER_NOT_FOUND));

        LocalDateTime startDate = DateUtils.adjustStartDateTime(request.getStartDate(), request.isAllDay());
        LocalDateTime endDate = DateUtils.adjustEndDateTime(request.getEndDate(), request.isAllDay());
        ClassLog classLog = ClassLog.builder()
                .user(user)
                .title(request.getTitle())
                .startDate(startDate)
                .endDate(endDate)
                .classContents(request.getClassContents())
                .plan(request.getPlan())
                .submission(request.getSubmission())
                .magnitude(request.getMagnitude())
                .classLogImage(new ArrayList<>()) // 이미지 리스트 초기화
                .build();

        if (classLogImages != null && !classLogImages.isEmpty()) {
            List<ClassLogImage> uploadedImages = uploadClassLogImages(classLog, classLogImages);
            classLog.getClassLogImage().addAll(uploadedImages); // 이미지 리스트에 추가
        }
        uploadClassLogImages(classLog, classLogImages);
        return ClassLogResponseDto.of(classLogRepository.save(classLog));
    }

    public ClassLogDeleteResponseDto deleteClassLog(Long userId, Long classLogId) {
        ClassLog classLog = classLogRepository.findByIdAndUserId(userId, classLogId).orElseThrow();
        classLogRepository.delete(classLog);

        return ClassLogDeleteResponseDto.builder()
                .id(classLog.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ClassLogResponseDto> readAllClassLog(Long userId) {
        //todo slice 형태로 바꿔야합니다
        List<ClassLog> classLogs = classLogRepository.findAllByUserId(userId);

        return classLogs.stream()
                .map(ClassLogResponseDto::of)
                .toList();
    }

    @Transactional(readOnly = true)
    public ClassLogDetailResponseDto getClassLogDetail(Long userId, Long classLogId) {
        ClassLog classLog = classLogRepository.findByIdAndUserId(userId, classLogId).orElseThrow();
        return new ClassLogDetailResponseDto(classLog);
    }

    public ClassLogResponseDto updateClassLog(Long userId, Long classLogId,
                                              ClassLogUpdateRequestDto classLogUpdateRequestDto) {
        ClassLog classLog = classLogRepository.findByIdAndUserId(userId, classLogId).orElseThrow();
        updateEachClassLogItem(classLogUpdateRequestDto, classLog);

        return ClassLogResponseDto.of(classLog);
    }

    private void updateEachClassLogItem(ClassLogUpdateRequestDto classLogUpdateRequestDto, ClassLog classLog) {
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
        //todo 이미지에 대한 수정부분도 필요합니다.
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


}
