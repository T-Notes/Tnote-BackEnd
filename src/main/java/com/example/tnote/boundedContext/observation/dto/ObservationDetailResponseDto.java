package com.example.tnote.boundedContext.observation.dto;

import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.entity.ObservationImage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class ObservationDetailResponseDto {
    private Long id;
    private Long userId;
    private String studentName;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String observationContents; // 관찰 내용
    private String guidance; // 해석 및 지도 방안
    private List<String> observationImageUrls;

    public ObservationDetailResponseDto(Observation observation, List<ObservationImage> observationImages) {
        this.id = observation.getId();
        this.userId = observation.getUser().getId();
        this.studentName = observation.getStudentName();
        this.startDate = observation.getStartDate();
        this.endDate = observation.getEndDate();
        this.observationContents = observation.getObservationContents();
        this.guidance = observation.getGuidance();
        this.observationImageUrls = observationImages.stream().map(ObservationImage::getObservationImageUrl).toList();
    }
}
