package com.example.tnote.boundedContext.home.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LogsDeleteRequestDto {
    private final List<Long> classLogIds;
    private final List<Long> proceedingIds;
    private final List<Long> observationIds;
    private final List<Long> consultationIds;
}
