package com.example.tnote.boundedContext.observation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ObservationSliceResponseDto {
    List<ObservationResponseDto> observations;
    private int numberOfObservation;

    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;
}
