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

    public List<ClassLog> findAll(String title) {
        return query
                .selectFrom(classLog)
                .where(classLog.title.like("%" + title + "%"))
                .orderBy(classLog.id.desc())
                .fetch();
    }
}

