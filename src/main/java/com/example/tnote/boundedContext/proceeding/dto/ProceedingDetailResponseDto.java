package com.example.tnote.boundedContext.proceeding.dto;

import com.example.tnote.boundedContext.home.constant.LogType;
import com.example.tnote.boundedContext.proceeding.entity.Proceeding;
import com.example.tnote.boundedContext.proceeding.entity.ProceedingImage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ProceedingDetailResponseDto {
    private Long id;
    private Long userId;
    private String title;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String location;
    private String workContents;
    private List<String> proceedingImageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String logType;
    private List<ProceedingImageResponseDto> images;

    public ProceedingDetailResponseDto(Proceeding proceeding, List<ProceedingImage> proceedingImages) {
        this.id = proceeding.getId();
        this.userId = proceeding.getUser().getId();
        this.title = proceeding.getTitle();
        this.startDate = proceeding.getStartDate();
        this.endDate = proceeding.getEndDate();
        this.location = proceeding.getLocation();
        this.workContents = proceeding.getWorkContents();
        this.proceedingImageUrls = proceedingImages.stream().map(ProceedingImage::getProceedingImageUrl).toList();
        this.createdAt = proceeding.getCreatedAt();
        this.updatedAt = proceeding.getUpdatedAt();
        this.logType = LogType.PROCEEDING.name();
        this.images = proceedingImages.stream()
                .map(image -> new ProceedingImageResponseDto(image.getProceedingImageUrl(), image.getName()))
                .toList();
    }
}
