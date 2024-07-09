package com.example.tnote.boundedContext.consultation.repository.query;

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
    public List<Consultation> findAll(String keyword, Long scheduleId) {
        return query
                .selectFrom(consultation)
                .where(
                        consultation.schedule.id.eq(scheduleId)
                                .and(consultation.title.like("%" + keyword + "%"))
                )
                .orderBy(consultation.id.desc())
                .fetch();
    }

    public void deleteAllByScheduleIdAndUserId(Long scheduleId, Long userId) {
        query
                .selectFrom(consultation)
                .where(
                        consultation.schedule.id.eq(scheduleId)
                                .and(consultation.user.id.eq(userId))
                )
                .orderBy(consultation.id.desc())
                .fetch();
    }
}
