package com.example.tnote.boundedContext.classLog.service;

import com.example.tnote.base.exception.CommonErrorResult;
import com.example.tnote.base.exception.CommonException;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDetailResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogRequestDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogDeleteResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogUpdateRequestDto;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.repository.ClassLogRepository;
import com.example.tnote.boundedContext.user.entity.User;
import com.example.tnote.boundedContext.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
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
    //  class에 @Transactional 선언을 해주셨는데, 메소드에 read only가 아니게 재선언 하신게 뭔가 중복코드 같습니다!
    public ClassLogResponseDto save(Long userId, ClassLogRequestDto request) {
        // 사소한거긴 한데 여기서 Exception을 Common말고 UserException으로 잡아서 가독성을 높이면 어떨까라는 생각이 듭니다!
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CommonException(CommonErrorResult.USER_NOT_FOUND));

        /*
            다른 service파일들을 보니 아래 "종일" 관련해서 localdatetime을 만드는 코드가 중복되어 보입니다!
            해당 부분은 static 메소드로 하나 만들어서 공유하면 어떨까라는 생각이 또 듭니다!
         */
        LocalDateTime startDate = request.getStartDate();
        LocalDateTime endDate = request.getEndDate();
        if(request.isAllDay()){
            startDate = startDate.withHour(12).withMinute(0);
            endDate = endDate.withHour(23).withMinute(59);
        }
        ClassLog classLog = ClassLog.builder()
                .user(user)
                .title(request.getTitle())
                .startDate(startDate)
                .endDate(endDate)
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

    public ClassLogResponseDto updateClassLog(Long userId, Long classLogId, ClassLogUpdateRequestDto classLogUpdateRequestDto){
        ClassLog classLog = classLogRepository.findByIdAndUserId(userId, classLogId).orElseThrow();
        updateEachClassLogItem(classLogUpdateRequestDto,classLog);

        return ClassLogResponseDto.of(classLog);
    }

    private void updateEachClassLogItem(ClassLogUpdateRequestDto classLogUpdateRequestDto , ClassLog classLog){
        if (classLogUpdateRequestDto.hasPlan()){
            classLog.updatePlan(classLogUpdateRequestDto.getPlan());
        }
        if (classLogUpdateRequestDto.hasSubmission()){
            classLog.updateSubmission(classLogUpdateRequestDto.getSubmission());
        }
        if (classLogUpdateRequestDto.hasClassContents()){
            classLog.updateClassContents(classLogUpdateRequestDto.getClassContents());
        }
        if (classLogUpdateRequestDto.hasMagnitude()){
            classLog.updateMagnitude(classLogUpdateRequestDto.getMagnitude());
        }
        //todo 이미지에 대한 수정부분도 필요합니다.
    }
}
