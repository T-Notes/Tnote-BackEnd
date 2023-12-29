package com.example.tnote.boundedContext.classLog.service;

import com.example.tnote.base.exception.CommonErrorResult;
import com.example.tnote.base.exception.CommonException;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.repository.ClassLogRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class ClassLogService {
    private final ClassLogRepository classLogRepository;
    private final UserRepository userRepository;

    @Transactional
    public ClassLogResponseDto save(Long userId, ClassLogRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(CommonErrorResult.USER_NOT_FOUND));

        ClassLog classLog = ClassLog.builder()
                .user(user)
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .classContents(request.getClassContents())
                .plan(request.getPlan())
                .submission(request.getSubmission())
                .magnitude(request.getMagnitude())
                .build();
        return ClassLogResponseDto.of(classLogRepository.save(classLog));
    }

    public ClassLogDeleteResponseDto deleteClassLog(Long userId, Long classLogId) {
        ClassLog classLog = classLogRepository.findByIdAndUserId(userId, classLogId).orElseThrow();
        classLogRepository.delete(classLog);

        return ClassLogDeleteResponseDto.builder()
                .id(classLog.getId())
                .build();
    }
}
