package com.example.tnote.boundedContext.classLog.dto;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassLogResponseDto {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String plan;
    private String classContents;
    private String submission;
    private String magnitude;

    public static ClassLogResponseDto of(ClassLog classLog) {
        return ClassLogResponseDto.builder()
                .id(classLog.getId())
                .title(classLog.getTitle())
                .startDate(classLog.getStartDate())
                .endDate(classLog.getEndDate())
                .plan(classLog.getPlan())
                .classContents(classLog.getClassContents())
                .submission(classLog.getSubmission())
                .magnitude(classLog.getMagnitude())
                .build();
    }

    public static ClassLog toEntity(ClassLogResponseDto response) {
        return ClassLog.builder()
                .id(response.getId())
                .title(response.getTitle())
                .startDate(response.getStartDate())
                .endDate(response.getEndDate())
                .plan(response.getPlan())
                .classContents(response.getClassContents())
                .submission(response.getSubmission())
                .magnitude(response.getMagnitude())
                .build();
    }
}
