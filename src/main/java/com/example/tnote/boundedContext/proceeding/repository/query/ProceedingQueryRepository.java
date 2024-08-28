package com.example.tnote.boundedContext.proceeding.repository.query;

import static com.example.tnote.boundedContext.proceeding.entity.QProceeding.proceeding;

import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponse;
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
    public List<ProceedingResponse> findAll(Long userId, String keyword, Long scheduleId) {
        List<Proceeding> proceedings = query
                .selectFrom(proceeding)
                .where(
                        proceeding.schedule.id.eq(scheduleId)
                                .and(proceeding.title.like("%" + keyword + "%"))
                                .and(proceeding.user.id.eq(userId))
                )
                .orderBy(proceeding.id.desc())
                .fetch();

        return proceedings.stream()
                .map(ProceedingResponse::from)
                .toList();
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