package com.example.tnote.boundedContext.archive.repository;

import static com.example.tnote.boundedContext.proceeding.entity.QProceeding.proceeding;

import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProceedingQueryRepository {
    private final JPAQueryFactory query;

    // 작성 시간을 id의 역순으로 배치
    public List<Proceeding> findAll(String keyword, Long scheduleId) {
        return query
                .selectFrom(proceeding)
                .where(
                        proceeding.schedule.id.eq(scheduleId)
                                .and(proceeding.title.like("%" + keyword + "%")))
                .orderBy(proceeding.id.desc())
                .fetch();
    }

    public void deleteAllByScheduleIdAndUserId(Long scheduleId, Long userId) {
        query
                .selectFrom(proceeding)
                .where(
                        proceeding.schedule.id.eq(scheduleId)
                                .and(proceeding.user.id.eq(userId))
                )
                .orderBy(proceeding.id.desc())
                .fetch();
    }
}