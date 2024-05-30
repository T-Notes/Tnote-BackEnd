package com.example.tnote.boundedContext.home.repository;

import static com.example.tnote.boundedContext.classLog.entity.QClassLog.classLog;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ClassLogQueryRepository {
    private final JPAQueryFactory query;

    // 작성 시간을 id의 역순으로 배치
    public List<ClassLog> findAll(String keyword, Long scheduleId) {
        return query
                .selectFrom(classLog)
                .where(
                        classLog.schedule.id.eq(scheduleId)
                                .and(classLog.title.like("%" + keyword + "%"))
                )
                .orderBy(classLog.id.desc())
                .fetch();
    }

    public void deleteAllByScheduleIdAndUserId(Long scheduleId, Long userId) {
        query
                .selectFrom(classLog)
                .where(
                        classLog.schedule.id.eq(scheduleId)
                                .and(classLog.user.id.eq(userId))
                )
                .orderBy(classLog.id.desc())
                .fetch();
    }
}

