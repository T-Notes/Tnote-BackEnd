package com.example.tnote.boundedContext.observation.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ObservationImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "observation_image_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String observationImageUrl;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observation_id")
    private Observation observation;
    @Column(name = "original_file_name")
    private String name;
    @Version
    private Long version;
    @Builder
    public ObservationImage(Observation observation, String observationImageUrl, String originalFileName) {
        this.observation = observation;
        if (observation != null) {
            observation.getObservationImage().add(this);
        }
        this.observationImageUrl = observationImageUrl;
        this.name = originalFileName;
    }
}
