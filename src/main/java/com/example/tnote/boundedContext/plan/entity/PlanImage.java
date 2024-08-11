package com.example.tnote.boundedContext.plan.entity;

import com.example.tnote.base.entity.BaseTimeEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "PLAN_IMAGE_TB")
public class PlanImage extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_image_id")
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String planImageUrl;

    @Column(name = "original_file_name")
    private String name;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;

    public PlanImage() {
    }

    public PlanImage(String planImageUrl, String name, Plan plan) {
        this.planImageUrl = planImageUrl;
        this.name = name;
        this.plan = plan;
    }
}
