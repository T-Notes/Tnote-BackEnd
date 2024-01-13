package com.example.tnote.boundedContext.proceeding.entity;

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
public class ProceedingImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proceeding_image_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String proceedingImageUrl;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "proceeding_id")
    private Proceeding proceeding;

    @Builder
    public ProceedingImage(Proceeding proceeding, String proceedingImageUrl) {
        this.proceeding = proceeding;
        if (proceeding != null) {
            proceeding.getProceedingImage().add(this);
        }
        this.proceedingImageUrl = proceedingImageUrl;
    }
}
