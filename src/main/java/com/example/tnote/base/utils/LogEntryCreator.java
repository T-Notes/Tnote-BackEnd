package com.example.tnote.base.utils;

import static com.example.tnote.base.exception.ErrorCodes.NOT_VALID_FIELD;

import com.example.tnote.base.exception.CustomExceptions;
import com.example.tnote.boundedContext.recentLog.dto.RecentLogResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class LogEntryCreator {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    public static String createLogEntry(Long logId, String logType, double score) {
        try {
            Map<String, Object> logEntryMap = new HashMap<>();
            logEntryMap.put("logId", logId);
            logEntryMap.put("logType", logType);
            logEntryMap.put("timestamp", Instant.ofEpochMilli((long) score));

            // Map 객체를 JSON 문자열로 변환
            return objectMapper.writeValueAsString(logEntryMap);
        } catch (Exception e) {
            throw new CustomExceptions(NOT_VALID_FIELD);
        }
    }

    public static RecentLogResponse convertToDto(String logEntry) {
        try {
            return objectMapper.readValue(logEntry, RecentLogResponse.class);
        } catch (Exception e) {
            throw new CustomExceptions(NOT_VALID_FIELD);
        }
    }
}
