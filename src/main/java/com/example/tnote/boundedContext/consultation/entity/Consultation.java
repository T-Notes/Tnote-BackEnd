package com.example.tnote.boundedContext.consultation.entity;

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
@Table(name = "CONSULTATION_TB")
public class Consultation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consultation_id")
    private Long id;
    private String studentName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CounselingField counselingField;
    private CounselingType counselingType;
    private String consultationContents;
    private String consultationResult;
    @JsonManagedReference
    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL)
    private List<ConsultationImage> consultationImage = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateConsultationContents(String consultationContents) {
        this.consultationContents = consultationContents;
    }
    public void clearConsultationImages() {
        this.consultationImage.clear();
    }
    public void updateConsultationResult(String consultationResult) {
        this.consultationResult = consultationResult;
    }
    public void updateConsultationImages(List<ConsultationImage> consultationImages){
        this.consultationImage = consultationImages;
    }
}
