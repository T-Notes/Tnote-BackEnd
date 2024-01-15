package com.example.tnote.boundedContext.observation.repository;

import com.example.tnote.boundedContext.observation.entity.ObservationImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObservationImageRepository extends JpaRepository<ObservationImage, Long> {
}
