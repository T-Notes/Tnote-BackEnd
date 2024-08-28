package com.example.tnote.boundedContext.observation.repository.query;

import static com.example.tnote.boundedContext.observation.entity.QObservation.observation;

import com.example.tnote.boundedContext.observation.dto.ObservationResponse;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ObservationQueryRepository {
    private final JPAQueryFactory query;

    // 작성 시간을 id의 역순으로 배치
    public List<ObservationResponse> findAll(Long userId, String keyword, Long scheduleId) {
        List<Observation> observations = query
                .selectFrom(observation)
                .where(
                        observation.schedule.id.eq(scheduleId)
                                .and(observation.title.like("%" + keyword + "%"))
                                .and(observation.user.id.eq(userId))
                )
                .orderBy(observation.id.desc())
                .fetch();

        return observations.stream()
                .map(ObservationResponse::from)
                .toList();
    }

    public void deleteAllByScheduleIdAndUserId(Long scheduleId, Long userId) {
        query
                .selectFrom(observation)
                .where(
                        observation.schedule.id.eq(scheduleId)
                                .and(observation.user.id.eq(userId))
                )
                .orderBy(observation.id.desc())
                .fetch();
    }
}
