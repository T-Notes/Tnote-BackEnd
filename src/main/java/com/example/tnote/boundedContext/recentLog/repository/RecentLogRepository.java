package com.example.tnote.boundedContext.recentLog.repository;

import com.example.tnote.boundedContext.recentLog.entity.RecentLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecentLogRepository extends JpaRepository<RecentLog, Long> {
    @Query(value = "SELECT rl.* FROM recent_log rl INNER JOIN "
            + "(SELECT log_id, MAX(timestamp) AS max_timestamp FROM recent_log WHERE user_id = :userId GROUP BY log_id)"
            + " as latest_logs ON rl.log_id = latest_logs.log_id AND rl.timestamp = latest_logs.max_timestamp "
            + "WHERE rl.user_id = :userId ORDER BY rl.timestamp DESC LIMIT 4", nativeQuery = true)
    List<RecentLog> findTop4DistinctByUserId(Long userId);

    void deleteByLogIdAndLogType(Long logId, String logType);


}
