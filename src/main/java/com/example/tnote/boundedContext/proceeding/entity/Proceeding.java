package com.example.tnote.boundedContext.proceeding.entity;

import com.example.tnote.base.entity.BaseTimeEntity;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "PROCEEDING_TB")
public class Proceeding extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proceeding_id")
    private Long id;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String workContents;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

//    @JsonManagedReference
//    @OneToMany(mappedBy = "proceeding", cascade = CascadeType.ALL)
//    private List<ProceedingImage> proceedingImage = new ArrayList<>();
//
//    public void clearProceedingImages() {
//        this.proceedingImage.clear();
//    }

    public void updateLocation(String location) {
        this.location = location;
    }

    public void updateWorkContents(String workContents) {
        this.workContents = workContents;
    }
}
