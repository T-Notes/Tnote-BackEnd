package com.example.tnote.boundedContext.proceeding.entity;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "PROCEEDING_TB")
public class Proceeding extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proceeding_id")
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String workContents;
    private String color; // 일지 색상

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @JsonManagedReference
    @OneToMany(mappedBy = "proceeding", cascade = CascadeType.ALL)
    private List<ProceedingImage> proceedingImage = new ArrayList<>();

    public void clearProceedingImages() {
        this.proceedingImage.clear();
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void updateEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void updateLocation(String location) {
        this.location = location;
    }

    public void updateWorkContents(String workContents) {
        this.workContents = workContents;
    }

    public void updateProceedingImage(List<ProceedingImage> proceedingImage) {
        this.proceedingImage = proceedingImage;
    }

    public Proceeding() {
    }

    public Proceeding(final Long id, final String title, final LocalDateTime startDate, final LocalDateTime endDate,
                      final String location, final String workContents, final String color,
                      final User user, final Schedule schedule, final List<ProceedingImage> proceedingImage) {
        this.id = id;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
        this.workContents = workContents;
        this.color = color;
        this.user = user;
        this.schedule = schedule;
        this.proceedingImage = proceedingImage;
    }
}
