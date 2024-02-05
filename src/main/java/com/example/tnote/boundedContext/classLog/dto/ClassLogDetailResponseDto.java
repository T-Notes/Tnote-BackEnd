package com.example.tnote.boundedContext.classLog.dto;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.example.tnote.boundedContext.classLog.entity.ClassLogImage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class ClassLogDetailResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String plan;
    private String classContents;
    private String submission;
    private String magnitude;
    private List<String> classLogImageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ClassLogDetailResponseDto(ClassLog classLog, List<ClassLogImage> classLogImages) {
        this.id = classLog.getId();
        this.userId = classLog.getUser().getId();
        this.title = classLog.getTitle();
        this.startDate = classLog.getStartDate();
        this.endDate = classLog.getEndDate();
        this.plan = classLog.getPlan();
        this.classContents = classLog.getClassContents();
        this.submission = classLog.getSubmission();
        this.magnitude = classLog.getMagnitude();
        this.classLogImageUrls = classLogImages.stream()
                .map(ClassLogImage::getClassLogImageUrl)
                .toList();
        this.createdAt = classLog.getCreatedAt();
        this.updatedAt = classLog.getUpdatedAt();
    }
}
