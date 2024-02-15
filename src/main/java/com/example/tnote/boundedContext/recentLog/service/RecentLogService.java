package com.example.tnote.boundedContext.recentLog.service;

import static com.example.tnote.base.utils.LogEntryCreator.createLogEntry;

import com.example.tnote.base.utils.LogEntryCreator;
import com.example.tnote.boundedContext.home.dto.ArchiveResponseDto;
import com.example.tnote.boundedContext.home.dto.RecentLogResponseDto;
import com.example.tnote.boundedContext.recentLog.repository.RecentLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecentLogService {
    private static final String RECENT_LOGS_KEY_PREFIX = "recentLogs:";
    private static final int MAX_RECENT_LOGS = 4;
    private final RedisTemplate<String, String> redisTemplate;
    private final RecentLogRepository recentLogRepository;
    private final ObjectMapper objectMapper;

    public void saveRecentLog(Long userId, Long logId, String logType) {
        String key = RECENT_LOGS_KEY_PREFIX + userId;
        double score = System.currentTimeMillis();
        String value = createLogEntry(logId, logType, score);

        redisTemplate.opsForZSet().add(key, value, score);

        long currentCount = redisTemplate.opsForZSet().size(key);
        if (currentCount > MAX_RECENT_LOGS) {
            redisTemplate.opsForZSet().removeRange(key, 0, currentCount - MAX_RECENT_LOGS - 1);
        }
    }

    public List<RecentLogResponseDto> getRecentLogs(Long userId) {
        String key = RECENT_LOGS_KEY_PREFIX + userId;
        Set<String> logEntries = redisTemplate.opsForZSet().reverseRange(key, 0, -1);

        return logEntries.stream()
                .map(LogEntryCreator::convertToDto)
                .filter(Objects::nonNull) // 변환 실패한 항목은 제외
                .toList();
    }
}
