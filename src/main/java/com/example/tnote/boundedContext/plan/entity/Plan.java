package com.example.tnote.boundedContext.plan.entity;

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
@Table(name = "PLAN_TB")
public class Plan extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    private String title;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private String location;

    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;


    @JsonManagedReference
    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL)
    private List<PlanImage> planImages = new ArrayList<>();

    public Plan() {
    }

    public Plan(final String title, final LocalDateTime startDate,
                final LocalDateTime endDate, final String contents, final String location, final User user,
                final Schedule schedule, final List<PlanImage> planImages) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.contents = contents;
        this.location = location;
        this.user = user;
        this.schedule = schedule;
        this.planImages = planImages;
    }

    public void clearImages() {
        this.planImages.clear();
    }
}
