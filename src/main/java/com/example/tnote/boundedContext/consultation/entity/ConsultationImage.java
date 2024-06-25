package com.example.tnote.boundedContext.consultation.entity;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultationImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "consultation_image_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String consultationImageUrl;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id")
    private Consultation consultation;
    @Column(name = "original_file_name")
    private String name;
    @Builder
    public ConsultationImage(Consultation consultation, String consultationImageUrl, String originalFileName) {
        this.consultation = consultation;
        if (consultation != null) {
            consultation.getConsultationImage().add(this);
        }
        this.consultationImageUrl = consultationImageUrl;
        this.name = originalFileName;
    }
}
