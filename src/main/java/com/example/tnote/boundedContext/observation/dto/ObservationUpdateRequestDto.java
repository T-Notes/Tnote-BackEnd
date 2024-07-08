package com.example.tnote.boundedContext.observation.dto;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class ObservationUpdateRequestDto {
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String observationContents; // 관찰 내용
    private String guidance; // 해석 및 지도 방안

    public boolean hasStudentName() {
        return title != null;
    }

    public boolean hasStartDate() {
        return startDate != null;
    }

    public boolean hasEndDate() {
        return endDate != null;
    }

    public boolean hasObservationContents() {
        return observationContents != null;
    }

    public boolean hasGuidance() {
        return guidance != null;
    }
}
