package com.example.tnote.boundedContext.home.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UnifiedLogResponseDto {
    private List<LogEntry> logs;
    private int totalLog;

    public static UnifiedLogResponseDto from(List<LogEntry> logs) {
        return UnifiedLogResponseDto.builder()
                .logs(logs)
                .totalLog(logs.size())
                .build();
    }
}
