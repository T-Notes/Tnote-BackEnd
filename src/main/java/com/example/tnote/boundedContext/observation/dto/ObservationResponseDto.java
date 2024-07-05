package com.example.tnote.boundedContext.observation.dto;

import com.example.tnote.boundedContext.archive.constant.LogType;
import com.example.tnote.boundedContext.archive.dto.LogEntry;
import com.example.tnote.boundedContext.observation.entity.Observation;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ObservationResponseDto implements LogEntry {
    private Long id;
    private String studentName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String observationContents; // 관찰 내용
    private String guidance; // 해석 및 지도 방안
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String logType;
    private String color;
    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public static ObservationResponseDto of(Observation observation) {
        return ObservationResponseDto.builder()
                .id(observation.getId())
                .studentName(observation.getStudentName())
                .startDate(observation.getStartDate())
                .endDate(observation.getEndDate())
                .observationContents(observation.getObservationContents())
                .guidance(observation.getGuidance())
                .createdAt(observation.getCreatedAt())
                .updatedAt(observation.getUpdatedAt())
                .logType(LogType.OBSERVATION.name())
                .color(observation.getColor())
                .build();
    }
}
