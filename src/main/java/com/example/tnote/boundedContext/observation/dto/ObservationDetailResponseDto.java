package com.example.tnote.boundedContext.observation.dto;

import com.example.tnote.boundedContext.archive.constant.LogType;
import com.example.tnote.boundedContext.observation.entity.Observation;
import com.example.tnote.boundedContext.observation.entity.ObservationImage;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Getter;

@Getter
public class ObservationDetailResponseDto {
    private final Long id;
    private final Long userId;
    private final String title;
    private final LocalDateTime startDate;
    private final LocalDateTime endDate;
    private final String observationContents; // 관찰 내용
    private final String guidance; // 해석 및 지도 방안
    private final List<String> observationImageUrls;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final String logType;
    private final List<ObservationImageResponseDto> images;

    public ObservationDetailResponseDto(Observation observation, List<ObservationImage> observationImages) {
        this.id = observation.getId();
        this.userId = observation.getUser().getId();
        this.title = observation.getTitle();
        this.startDate = observation.getStartDate();
        this.endDate = observation.getEndDate();
        this.observationContents = observation.getObservationContents();
        this.guidance = observation.getGuidance();
        this.observationImageUrls = observationImages.stream().map(ObservationImage::getObservationImageUrl).toList();
        this.createdAt = observation.getCreatedAt();
        this.updatedAt = observation.getUpdatedAt();
        this.logType = LogType.OBSERVATION.name();
        this.images = observationImages.stream()
                .map(image -> new ObservationImageResponseDto(image.getObservationImageUrl(), image.getName()))
                .toList();
    }
}
