package com.example.tnote.boundedContext.schedule.entity;

import com.example.tnote.base.entity.BaseTimeEntity;
import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.subject.entity.Subjects;
import com.example.tnote.boundedContext.todo.entity.Todo;
import com.example.tnote.boundedContext.user.entity.User;
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
import java.time.LocalDate;
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
@Table(name = "SCHEDULE_TB")
public class Schedule extends BaseTimeEntity {
    /*
        Schedule [1] : Subjects [N]
        Schedule [1] : Todo [N]
        Schedule [1] : ClassLog [N]
        Schedule [1] : Observation [N]
        Schedule [1] : Proceeding [N]
        Schedule [1] : Consultation [N]
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;
    private String semesterName;
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

    @Builder.Default
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ClassLog> classLogList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Observation> observationList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Proceeding> proceedingList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Consultation> consultationList = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Todo> todoList = new ArrayList<>();

    public void updateSemesterName(String semesterName) {
        this.semesterName = semesterName;
    }

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
