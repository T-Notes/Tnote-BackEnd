package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.boundedContext.home.constant.LogType;
import com.example.tnote.boundedContext.home.dto.LogEntry;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProceedingResponseDto implements LogEntry {
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

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public static ProceedingResponseDto of(Proceeding proceeding) {
        return ProceedingResponseDto.builder()
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
                .build();
    }
}
