package com.example.tnote.boundedContext.consultation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ConsultationSliceResponseDto {

    List<ConsultationResponse> consultations;
    private int numberOfConsultation;

    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;
}
