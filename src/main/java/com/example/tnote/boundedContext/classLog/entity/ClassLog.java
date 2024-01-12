package com.example.tnote.boundedContext.classLog.entity;

import com.example.tnote.base.entity.BaseTimeEntity;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CLASS_LOG_TB")
public class ClassLog extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "class_log_id")
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private LocalDateTime startDate;
    @Column(nullable = false)
    private LocalDateTime endDate;
    private String plan; // 학습계획
    private String classContents; // 수업내용
    private String submission; // 제출과제
    private String magnitude; // 진도

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @JsonManagedReference
    @OneToMany(mappedBy = "classLog", cascade = CascadeType.ALL)
    private List<ClassLogImage> classLogImage = new ArrayList<>();

    public void clearClassLogImages() {
        this.classLogImage.clear();
    }

    public void updatePlan(String plan) {
        this.plan = plan;
    }

    public void updateClassContents(String classContents) {
        this.classContents = classContents;
    }

    public void updateSubmission(String submission) {
        this.submission = submission;
    }

    public void updateMagnitude(String magnitude) {
        this.magnitude = magnitude;
    }
}
