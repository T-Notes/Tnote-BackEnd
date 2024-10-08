package com.example.tnote.boundedContext.recentLog.service;

import com.example.tnote.boundedContext.recentLog.dto.RecentLogResponse;
import com.example.tnote.boundedContext.recentLog.dto.RecentLogResponses;
import com.example.tnote.boundedContext.recentLog.entity.RecentLog;
import com.example.tnote.boundedContext.recentLog.repository.RecentLogRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RecentLogService {
    private final RecentLogRepository recentLogRepository;

    public RecentLogService(final RecentLogRepository recentLogRepository) {
        this.recentLogRepository = recentLogRepository;
    }


    public void save(final Long userId, final Long logId, final Long scheduleId, final String logType) {
        RecentLog recentLog = new RecentLog(userId, logId, logType, Instant.now(), scheduleId);
        recentLogRepository.save(recentLog);
    }

    public RecentLogResponses find(final Long userId, final Long scheduleId) {
        List<RecentLog> recentLogs = recentLogRepository.findTop4DistinctByUserIdAndScheduleId(userId, scheduleId);
        return RecentLogResponses.from(recentLogs);
    }

    public void delete(final Long logId, final String logType) {
        recentLogRepository.deleteByLogIdAndLogType(logId, logType);
    }
}
