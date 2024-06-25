package com.example.tnote.boundedContext.observation.repository;

import com.example.tnote.boundedContext.observation.entity.ObservationImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ObservationImageRepository extends JpaRepository<ObservationImage, Long> {
    List<ObservationImage> findObservationImageByObservationId(Long observationId);
    @Modifying
    @Query("DELETE FROM ObservationImage oi WHERE oi.observation.id = :observationId")
    void deleteByObservationId(Long observationId);
}
