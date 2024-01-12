package com.example.tnote.boundedContext.home.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ObservationQueryRepository {
    private final JPAQueryFactory query;

    // TODO ; observation 다 만들어지면 이거 열면 된다.
//    public List<Observation> findAll(String studentName) {
//        return query
//                .selectFrom(obsevation)
//                .where(obsevation.studentName.like("%" + studentName + "%"))
//                .orderBy(obsevation.id.desc())
//                .fetch();
//    }
}
