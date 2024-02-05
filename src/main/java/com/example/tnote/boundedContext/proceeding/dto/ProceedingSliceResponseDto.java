package com.example.tnote.boundedContext.proceeding.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProceedingSliceResponseDto {
    List<ProceedingResponseDto> proceedings;
    private int numberOfProceeding;

    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;
}
