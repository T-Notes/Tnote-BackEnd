package com.example.tnote.boundedContext.classLog.dto;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.archive.constant.LogType;
import com.example.tnote.boundedContext.archive.dto.LogEntry;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassLogResponse implements LogEntry {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String plan;
    private String classContents;
    private String submission;
    private String magnitude;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String logType;
    private String color;
    private List<ClassLogImage> classLogImages;
    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static ClassLogResponse from(final ClassLog classLog) {
        return ClassLogResponse.builder()
                .id(classLog.getId())
                .title(classLog.getTitle())
                .startDate(classLog.getStartDate())
                .endDate(classLog.getEndDate())
                .plan(classLog.getPlan())
                .classContents(classLog.getClassContents())
                .submission(classLog.getSubmission())
                .magnitude(classLog.getMagnitude())
                .createdAt(classLog.getCreatedAt())
                .updatedAt(classLog.getUpdatedAt())
                .logType(LogType.CLASS_LOG.name())
                .color(classLog.getColor())
                .classLogImages(classLog.getClassLogImage())
                .build();
    }
}
