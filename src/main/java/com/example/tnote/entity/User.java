package com.example.tnote.entity;

import com.example.tnote.entity.constant.OauthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {
    @Id
    @Column(name = "user_id")
    private Long id;
    private String email;
    private String username;
    private String school;
    private String subject;
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
