package com.example.tnote.boundedContext.observation.entity;

import com.example.tnote.base.entity.BaseTimeEntity;
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

    // 전체적으로 이미지가 있는 entity JsonManagedRef를 붙여놓으셨는데, 순환참조 외에 특별한 이유가 있으신지 궁금합니다!
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
