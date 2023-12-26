package com.example.tnote.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Schedule extends BaseTimeEntity {

    @Id
    @Column(name = "schedule_id")
    private Long id;
    private String semesterName;
    private String lastDay;
    private String lastPeriod;
    private String subjectName;
    private String classPeriod;
    private String classDay;
    private String classLocation;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
