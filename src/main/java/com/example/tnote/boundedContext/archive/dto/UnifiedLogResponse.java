package com.example.tnote.boundedContext.archive.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UnifiedLogResponse {
    private List<LogEntry> logs;
    private int totalLog;

    public static UnifiedLogResponse of(final List<LogEntry> logs, final int totalLog) {
        return UnifiedLogResponse.builder()
                .logs(logs)
                .totalLog(totalLog)
                .build();
    }
}
