package com.example.tnote.boundedContext.recentLog.repository;

import com.example.tnote.boundedContext.recentLog.entity.RecentLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecentLogRepository extends JpaRepository<RecentLog, Long> {
    List<RecentLog> findTop4ByUserIdOrderByTimestampDesc(Long userId);

}
