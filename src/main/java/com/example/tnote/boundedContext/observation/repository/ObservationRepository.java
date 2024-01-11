package com.example.tnote.boundedContext.observation.repository;

import com.example.tnote.boundedContext.observation.entity.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObservationRepository extends JpaRepository<Observation, Long> {
}
