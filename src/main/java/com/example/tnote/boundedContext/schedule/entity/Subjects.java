package com.example.tnote.boundedContext.schedule.entity;

import com.example.tnote.base.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="SUBJECTS_TB")
public class Subjects extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subject_id")
    private Long id;

    private String subjectName;
    private String classTime;
    private ClassDay classDay;
    private String classLocation;
    private String memo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    public void updateSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
    public void updateClassDay(ClassDay classDay) {
        this.classDay = classDay;
    }
    public void updateClassTime(String classTime) {
        this.classTime = classTime;
    }
    public void updateClassLocation(String classLocation) {
        this.classLocation = classLocation;
    }
    public void updateMemo(String memo) {
        this.memo = memo;
    }
}
