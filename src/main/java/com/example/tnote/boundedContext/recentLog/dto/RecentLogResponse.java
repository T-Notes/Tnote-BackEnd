package com.example.tnote.boundedContext.recentLog.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecentLogResponse {
    private Long logId;
    private String logType;
    private Instant timestamp;
    @JsonCreator
    public RecentLogResponse(
            @JsonProperty("logId") Long logId,
            @JsonProperty("logType") String logType,
            @JsonProperty("timestamp") Instant timestamp) {
        this.logId = logId;
        this.logType = logType;
        this.timestamp = timestamp;
    }
}
