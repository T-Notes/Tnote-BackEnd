package com.example.tnote.boundedContext.recentLog.dto;

import static java.util.stream.Collectors.toList;

import com.example.tnote.boundedContext.recentLog.entity.RecentLog;
import java.util.List;
import lombok.Getter;

@Getter
public class RecentLogResponses {
    private List<RecentLogResponse> recentLogs;

    public RecentLogResponses() {
    }

    public RecentLogResponses(final List<RecentLogResponse> recentLogs) {
        this.recentLogs = recentLogs;
    }

    public static RecentLogResponses from(final List<RecentLog> recentLogs) {
        List<RecentLogResponse> responses = recentLogs.stream()
                .map(log -> new RecentLogResponse(log.getLogId(), log.getLogType(), log.getTimestamp()))
                .collect(toList());
        return new RecentLogResponses(responses);
    }
}
