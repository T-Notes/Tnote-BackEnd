package com.example.tnote.boundedContext.consultation.dto;

import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.ConsultationImage;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import com.example.tnote.boundedContext.archive.constant.LogType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class ConsultationDetailResponseDto {
    private final Long id;
    private final Long userId;
    private final String title;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final CounselingField counselingField;
    private final CounselingType counselingType;
    private final String consultationContents;
    private final String consultationResult;
    private final List<String> consultationImageUrls;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String logType;
    private final List<ConsultationImageResponseDto> images;

    public ConsultationDetailResponseDto(Consultation consultation, List<ConsultationImage> consultationImages) {
        this.id = consultation.getId();
        this.userId = consultation.getUser().getId();
        this.title = consultation.getTitle();
        this.startDate = consultation.getStartDate();
        this.endDate = consultation.getEndDate();
        this.counselingField = consultation.getCounselingField();
        this.consultationResult = consultation.getConsultationResult();
        this.counselingType = consultation.getCounselingType();
        this.consultationContents = consultation.getConsultationContents();
        this.consultationImageUrls = consultationImages.stream().map(ConsultationImage::getConsultationImageUrl)
                .toList();
        this.createdAt = consultation.getCreatedAt();
        this.updatedAt = consultation.getUpdatedAt();
        this.logType = LogType.CONSULTATION.name();
        this.images = consultationImages.stream()
                .map(image -> new ConsultationImageResponseDto(image.getConsultationImageUrl(), image.getName()))
                .toList();
    }
}
