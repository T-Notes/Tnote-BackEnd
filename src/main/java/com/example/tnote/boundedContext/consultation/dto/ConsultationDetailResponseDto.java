package com.example.tnote.boundedContext.consultation.dto;

import com.example.tnote.boundedContext.consultation.entity.Consultation;
import com.example.tnote.boundedContext.consultation.entity.ConsultationImage;
import com.example.tnote.boundedContext.consultation.entity.CounselingField;
import com.example.tnote.boundedContext.consultation.entity.CounselingType;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ConsultationDetailResponseDto {
    private Long id;
    private Long userId;
    private String studentName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private CounselingField counselingField;
    private CounselingType counselingType;
    private String consultationContents;
    private String consultationResult;
    private List<String> consultationImageUrls;

    public ConsultationDetailResponseDto(Consultation consultation, List<ConsultationImage> consultationImages) {
        this.id = consultation.getId();
        this.userId = consultation.getUser().getId();
        this.studentName = consultation.getStudentName();
        this.startDate = consultation.getStartDate();
        this.endDate = consultation.getEndDate();
        this.counselingField = consultation.getCounselingField();
        this.consultationResult = consultation.getConsultationResult();
        this.counselingType = consultation.getCounselingType();
        this.consultationContents = consultation.getConsultationContents();
        this.consultationImageUrls = consultationImages.stream().map(ConsultationImage::getConsultationImageUrl)
                .toList();
    }
}
