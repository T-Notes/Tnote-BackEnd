package com.example.tnote.boundedContext.recentLog.service;

import com.example.tnote.base.utils.LogEntryCreator;
import com.example.tnote.boundedContext.recentLog.dto.RecentLogResponse;
import com.example.tnote.boundedContext.recentLog.entity.RecentLog;
import com.example.tnote.boundedContext.recentLog.repository.RecentLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RecentLogService {
    private static final String RECENT_LOGS_KEY_PREFIX = "recentLogs:";
    private static final int MAX_RECENT_LOGS = 4;

    private final RedisTemplate<String, String> redisTemplate;
    private final RecentLogRepository recentLogRepository;
    private final ObjectMapper objectMapper;

    public RecentLogService(final RedisTemplate<String, String> redisTemplate,
                            final RecentLogRepository recentLogRepository,
                            final ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.recentLogRepository = recentLogRepository;
        this.objectMapper = objectMapper;
    }


    public void save(final Long userId, final Long logId, final Long scheduleId, final String logType) {
        RecentLog recentLog = new RecentLog(userId, logId, logType, Instant.now(), scheduleId);
        recentLogRepository.save(recentLog);
    }

    public List<RecentLogResponse> getRecentLogs(Long userId, Long scheduleId) {
        List<RecentLog> recentLogs = recentLogRepository.findTop4DistinctByUserIdAndScheduleId(userId, scheduleId);
        return recentLogs.stream()
                .map(log -> new RecentLogResponse(log.getLogId(), log.getLogType(), log.getTimestamp()))
                .toList();
    }

    public void deleteRecentLog(Long logId, String logType) {
        recentLogRepository.deleteByLogIdAndLogType(logId, logType);
    }
}
