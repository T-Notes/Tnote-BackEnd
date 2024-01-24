package com.example.tnote.boundedContext.home.repository;

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

    public List<Proceeding> findAll(String title) {
        return query
                .selectFrom(proceeding)
                .where(proceeding.title.like("%" + title + "%"))
                .orderBy(proceeding.id.desc())
                .fetch();
    }
}