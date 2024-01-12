package com.example.tnote.boundedContext.subject.repository;

import static com.example.tnote.boundedContext.schedule.entity.QSchedule.schedule;
import static com.example.tnote.boundedContext.subject.entity.QSubjects.subjects;

import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import com.example.tnote.boundedContext.subject.entity.Subjects;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SubjectQueryRepository {

    private final JPAQueryFactory query;

    public List<Subjects> findAllByScheduleIdAndUserIdAndClassDay(Long scheduleId, Long userId, ClassDay day) {
        return query
                .selectFrom(subjects)
                .where(
                        subjects.schedule.id.eq(scheduleId)
                                .and(subjects.schedule.user.id.eq(userId))
                                .and(subjects.classDay.eq(day))
                )
                .orderBy(schedule.id.desc())
                .fetch();

    }
}
