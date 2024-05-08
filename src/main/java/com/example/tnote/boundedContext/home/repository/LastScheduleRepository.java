package com.example.tnote.boundedContext.home.repository;

import com.example.tnote.boundedContext.home.entity.LastSchedule;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LastScheduleRepository extends JpaRepository<LastSchedule, Long> {

    Optional<LastSchedule> findByUserIdAndScheduleId(Long userId, Long ScheduleId);
}
