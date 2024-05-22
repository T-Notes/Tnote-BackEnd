package com.example.tnote.boundedContext.observation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ObservationImageResponseDto {
    private String url;
    private String originalFileName;
}
