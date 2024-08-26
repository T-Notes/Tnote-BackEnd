package com.example.tnote.boundedContext.archive.dto;

import lombok.Getter;

@Getter
public class LogsDeleteResponse {
    private int classLogCount;
    private int proceedingCount;
    private int observationCount;
    private int consultationCount;
    private int planCount;

    public LogsDeleteResponse() {
    }

    public LogsDeleteResponse(final int classLogCount, final int proceedingCount, final int observationCount,
                              final int consultationCount, final int planCount) {
        this.classLogCount = classLogCount;
        this.proceedingCount = proceedingCount;
        this.observationCount = observationCount;
        this.consultationCount = consultationCount;
        this.planCount = planCount;
    }

    public static LogsDeleteResponse of(final int classLogCount, final int proceedingCount,
                                        final int observationCount, final int consultationCount,
                                        final int planCount) {
        return new LogsDeleteResponse(classLogCount, proceedingCount, observationCount,
                consultationCount, planCount);
    }
}
