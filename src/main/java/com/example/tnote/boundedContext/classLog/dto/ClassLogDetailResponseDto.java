package com.example.tnote.boundedContext.classLog.dto;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ClassLogDetailResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String plan;
    private String classContents;
    private String submission;
    private String magnitude;

    public ClassLogDetailResponseDto(ClassLog classLog) {
        this.id = classLog.getId();
        this.userId = classLog.getUser().getId();
        this.title = classLog.getTitle();
        this.startDate = classLog.getStartDate();
        this.endDate = classLog.getEndDate();
        this.plan = classLog.getPlan();
        this.classContents = classLog.getClassContents();
        this.submission = classLog.getSubmission();
        this.magnitude = classLog.getMagnitude();
        //todo 추후 이미지 관련 추가해야 합니다.
    }
}
