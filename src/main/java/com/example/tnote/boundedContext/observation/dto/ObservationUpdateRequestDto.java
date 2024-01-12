package com.example.tnote.boundedContext.observation.dto;

import lombok.Getter;

@Getter
public class ObservationUpdateRequestDto {
    private String observationContents; // 관찰 내용
    private String guidance; // 해석 및 지도 방안

    public boolean hasObservationContents() {
        return observationContents != null;
    }

    public boolean hasGuidance() {
        return guidance != null;
    }
}
