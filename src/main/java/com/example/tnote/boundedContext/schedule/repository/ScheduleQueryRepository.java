package com.example.tnote.boundedContext.schedule.repository;

import static com.example.tnote.boundedContext.schedule.entity.QSchedule.schedule;

import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


@Slf4j
@Repository
@RequiredArgsConstructor
public class ScheduleQueryRepository {

    private final JPAQueryFactory query;

    public List<Schedule> findAllBySemesterName(String semesterName) {
        return query
                .selectFrom(schedule)
                .where(schedule.semesterName.like("%" + semesterName + "%"))
                .orderBy(schedule.id.desc())
                .fetch();
    }

    public List<Schedule> findAllByUserId(Long userId) {
        return query
                .selectFrom(schedule)
                .where(schedule.user.id.eq(userId))
                .orderBy(schedule.id.desc())
                .fetch();
    }

    public List<Schedule> findAllById(Long scheduleId) {
        return query
                .selectFrom(schedule)
                .where(schedule.id.eq(scheduleId))
                .orderBy(schedule.id.desc())
                .fetch();
    }

}
