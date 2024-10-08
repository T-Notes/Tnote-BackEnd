package com.example.tnote.boundedContext.observation.entity;

import com.example.tnote.base.entity.BaseTimeEntity;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.user.entity.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
@Table(name = "OBSERVATION_TB")
public class Observation extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "observation_id")
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String observationContents; // 관찰 내용
    private String guidance; // 해석 및 지도 방안
    private String color; // 일지 색상


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @JsonManagedReference
    @OneToMany(mappedBy = "observation", cascade = CascadeType.ALL)
    private List<ObservationImage> observationImage = new ArrayList<>();

    public void clearObservationImages() {
        this.observationImage.clear();
    }

    public void updateStudentName(String title) {
        this.title = title;
    }

    public void updateStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void updateEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
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

    public Observation() {
    }

    public Observation(final String title, final LocalDateTime startDate, final LocalDateTime endDate,
                       final String observationContents,
                       final String guidance, final String color, final User user, final Schedule schedule,
                       final List<ObservationImage> observationImage) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.observationContents = observationContents;
        this.guidance = guidance;
        this.color = color;
        this.user = user;
        this.schedule = schedule;
        this.observationImage = observationImage;
    }
}
