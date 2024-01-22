package com.example.tnote.boundedContext.observation.dto;

import com.example.tnote.boundedContext.observation.entity.Observation;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ObservationResponseDto {
    private Long id;
    private String studentName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String observationContents; // 관찰 내용
    private String guidance; // 해석 및 지도 방안
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

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
                .build();
    }
}
