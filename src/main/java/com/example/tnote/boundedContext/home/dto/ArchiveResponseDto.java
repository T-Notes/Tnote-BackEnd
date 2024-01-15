package com.example.tnote.boundedContext.home.dto;

import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArchiveResponseDto {
    private List<ClassLogResponseDto> classLogs;
    private List<ObservationResponseDto> observations;
    private List<ConsultationResponseDto> consultations;
    private List<ProceedingResponseDto> proceedings;
}
