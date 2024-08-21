package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.boundedContext.archive.constant.LogType;
import com.example.tnote.boundedContext.archive.dto.LogEntry;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.entity.ProceedingImage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProceedingResponse implements LogEntry {
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String workContents;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String logType;
    private String color;
    private List<ProceedingImage> proceedingImages;

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static ProceedingResponse from(final Proceeding proceeding) {
        return ProceedingResponse.builder()
                .id(proceeding.getId())
                .title(proceeding.getTitle())
                .startDate(proceeding.getStartDate())
                .endDate(proceeding.getEndDate())
                .location(proceeding.getLocation())
                .workContents(proceeding.getWorkContents())
                .createdAt(proceeding.getCreatedAt())
                .updatedAt(proceeding.getUpdatedAt())
                .logType(LogType.PROCEEDING.name())
                .color(proceeding.getColor())
                .proceedingImages(proceeding.getProceedingImage())
                .build();
    }
}
