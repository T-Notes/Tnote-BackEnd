package com.example.tnote.boundedContext.observation.entity;

import com.example.tnote.base.entity.BaseTimeEntity;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import com.example.tnote.boundedContext.user.entity.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "OBSERVATION_TB")
public class Observation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "observation_id")
    private Long id;
    private String studentName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String observationContents; // 관찰 내용
    private String guidance; // 해석 및 지도 방안

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @JsonManagedReference
    @OneToMany(mappedBy = "observation", cascade = CascadeType.ALL)
    private List<ObservationImage> observationImage = new ArrayList<>();

    public void clearObservationImages() {
        this.observationImage.clear();
    }


    public void updateObservationContents(String observationContents) {
        this.observationContents = observationContents;
    }

    public void updateGuidance(String guidance) {
        this.guidance = guidance;
    }

    public void updateObservationImage(List<ObservationImage> observationImages) {
        this.observationImage = observationImages;
    }
}
