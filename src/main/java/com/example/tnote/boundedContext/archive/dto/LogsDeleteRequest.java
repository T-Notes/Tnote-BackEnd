package com.example.tnote.boundedContext.archive.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LogsDeleteRequest {
    private final List<Long> classLogIds;
    private final List<Long> proceedingIds;
    private final List<Long> observationIds;
    private final List<Long> consultationIds;
    private final List<Long> planIds;

    public LogsDeleteRequest(final List<Long> classLogIds, final List<Long> proceedingIds,
                             final List<Long> observationIds,
                             final List<Long> consultationIds,
                             final List<Long> planIds) {
        this.classLogIds = classLogIds;
        this.proceedingIds = proceedingIds;
        this.observationIds = observationIds;
        this.consultationIds = consultationIds;
        this.planIds = planIds;
    }
}
