package com.example.tnote.boundedContext.schedule.entity;

import com.example.tnote.base.entity.BaseTimeEntity;
import com.example.tnote.boundedContext.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="SCHEDULE_TB")
public class Schedule extends BaseTimeEntity {
/*
    Schedule [1] : Subjects [N]
 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;
    private String semesterName;
    private ClassDay lastDay; // 삭제 예정
    private String lastClass;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 스케쥴이 삭제 및 수정이 되면, 관련된 과목들이 전부 수정 및 삭제되어야 한다.
    @Builder.Default
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Subjects> subjectsList = new ArrayList<>();

    public void updateSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }
    public void updateLastDay(ClassDay lastDay) {
        this.lastDay = lastDay;
    } // 삭제 예정
    public void updateLastClass(String lastClass) {
        this.lastClass = lastClass;
    }
    public void updateStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public void updateEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
