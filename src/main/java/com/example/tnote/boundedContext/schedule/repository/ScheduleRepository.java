package com.example.tnote.boundedContext.schedule.repository;

import com.example.tnote.boundedContext.schedule.entity.Schedule;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findAllById(Long scheduleId);
}
