package com.example.tnote.boundedContext.observation.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ObservationRequestDto {
    private String studentName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String observationContents; // 관찰 내용
    private String guidance; // 해석 및 지도 방안
    private boolean isAllDay;
}
