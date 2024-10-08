package com.example.tnote.boundedContext.consultation.entity;

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
@Table(name = "CONSULTATION_TB")
public class Consultation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consultation_id")
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CounselingField counselingField;
    private CounselingType counselingType;
    private String consultationContents;
    private String consultationResult;
    private String color; // 일지 색상

    @JsonManagedReference
    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    private List<ConsultationImage> consultationImage = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    public void updateStudentName(String title) {
        this.title = title;
    }

    public void updateStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void updateEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void updateCounselingField(CounselingField counselingField) {
        this.counselingField = counselingField;
    }

    public void updateCounselingType(CounselingType counselingType) {
        this.counselingType = counselingType;
    }

    public void updateConsultationContents(String consultationContents) {
        this.consultationContents = consultationContents;
    }

    public void clearConsultationImages() {
        this.consultationImage.clear();
    }

    public void updateConsultationResult(String consultationResult) {
        this.consultationResult = consultationResult;
    }

    public void updateConsultationImages(List<ConsultationImage> consultationImages) {
        this.consultationImage = consultationImages;
    }

    public Consultation() {
    }

    public Consultation(final String title, final LocalDateTime startDate, final LocalDateTime endDate,
                        final CounselingField counselingField,
                        final CounselingType counselingType, final String consultationContents,
                        final String consultationResult,
                        final String color, final List<ConsultationImage> consultationImage, final User user,
                        final Schedule schedule) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.counselingField = counselingField;
        this.counselingType = counselingType;
        this.consultationContents = consultationContents;
        this.consultationResult = consultationResult;
        this.color = color;
        this.consultationImage = consultationImage;
        this.user = user;
        this.schedule = schedule;
    }
}
