package com.example.tnote.boundedContext.home.repository;

import static com.example.tnote.boundedContext.consultation.entity.QConsultation.consultation;

import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ConsultationQueryRepository {
    private final JPAQueryFactory query;

    // 작성 시간을 id의 역순으로 배치
    public List<Consultation> findAll(String studentName) {
        return query
                .selectFrom(consultation)
                .where(consultation.studentName.like("%" + studentName + "%"))
                .orderBy(consultation.id.desc())
                .fetch();
    }
}
