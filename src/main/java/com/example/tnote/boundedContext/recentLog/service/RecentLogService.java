package com.example.tnote.boundedContext.recentLog.service;

import com.example.tnote.boundedContext.recentLog.dto.RecentLogResponse;
import com.example.tnote.boundedContext.recentLog.entity.RecentLog;
import com.example.tnote.boundedContext.recentLog.repository.RecentLogRepository;
import java.time.Instant;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
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

    public List<RecentLogResponse> find(final Long userId, final Long scheduleId) {
        List<RecentLog> recentLogs = recentLogRepository.findTop4DistinctByUserIdAndScheduleId(userId, scheduleId);
        return recentLogs.stream()
                .map(log -> new RecentLogResponse(log.getLogId(), log.getLogType(), log.getTimestamp()))
                .toList();
    }

    public void delete(final Long logId, final String logType) {
        recentLogRepository.deleteByLogIdAndLogType(logId, logType);
    }
}
