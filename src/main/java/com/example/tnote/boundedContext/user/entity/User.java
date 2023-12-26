package com.example.tnote.boundedContext.user.entity;

import com.example.tnote.base.entity.BaseTimeEntity;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.schedule.entity.Schedule;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="USER_TB")
public class User extends BaseTimeEntity {
    @Id
    @Column(name = "user_id")
    private Long id;

    private String email;
    private String username;
    private String school;
    private String subject;
    private boolean alarm; // 마이페이지 > 설정 > 알림 받기

    @Enumerated(EnumType.STRING)
    private OauthProvider oauthProvider;

    @OneToMany(mappedBy = "user")
    private List<ClassLog> classLogs = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<Consultation> consultations = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<Observation> observations = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<Proceeding> proceedings = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<Schedule> schedules = new ArrayList<>();
}
