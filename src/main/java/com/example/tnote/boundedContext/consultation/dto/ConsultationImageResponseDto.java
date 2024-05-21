package com.example.tnote.boundedContext.consultation.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsultationImageResponseDto {
    private String url;
    private String originalFileName;
}
