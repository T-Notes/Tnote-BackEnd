package com.example.tnote.boundedContext.home.repository;

import static com.example.tnote.boundedContext.observation.entity.QObservation.observation;

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
    public List<Observation> findAll(String keyword) {
        return query
                .selectFrom(observation)
                .where(observation.studentName.like("%" + keyword + "%"))
                .orderBy(observation.id.desc())
                .fetch();
    }
}
