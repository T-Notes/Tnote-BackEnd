package com.example.tnote.boundedContext.recentLog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
public class RecentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recentLog_id")
    private Long id;

    private Long userId; // 사용자 ID

    private Long logId; // 로그 ID

    private String logType; // 로그 타입

    private Instant timestamp; // 로그가 기록된 시간
    private Long scheduleId;

    public RecentLog() {
    }

    public RecentLog(final Long userId, final Long logId, final String logType, final Instant timestamp,
                     final Long scheduleId) {
        this.userId = userId;
        this.logId = logId;
        this.logType = logType;
        this.timestamp = timestamp;
        this.scheduleId = scheduleId;
    }
}
