package com.example.tnote.boundedContext.proceeding.service;

import com.example.tnote.base.exception.CommonErrorResult;
import com.example.tnote.base.exception.CommonException;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingRequestDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.repository.ProceedingRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class ProceedingService {
    private final UserRepository userRepository;
    private final ProceedingRepository proceedingRepository;

    @Transactional
    public ProceedingResponseDto save(Long userId, ProceedingRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(CommonErrorResult.USER_NOT_FOUND));
        //Todo 종일이라는 체크박스에대한 시간값을 어떻게 넘겨줄지 회의가 필요합니다
        Proceeding proceeding = Proceeding.builder()
                .user(user)
                .title(request.getTitle())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .location(request.getLocation())
                .workContents(request.getWorkContents())
                .build();
        return ProceedingResponseDto.of(proceedingRepository.save(proceeding));
    }
    @Transactional(readOnly = true)
    public List<ProceedingResponseDto> readAllClassLog(Long userId) {
        //todo slice 형태로 바꿔야합니다
        List<Proceeding> proceedings = proceedingRepository.findAllByUserId(userId);

        return proceedings.stream()
                .map(ProceedingResponseDto::of)
                .toList();
    }

}
