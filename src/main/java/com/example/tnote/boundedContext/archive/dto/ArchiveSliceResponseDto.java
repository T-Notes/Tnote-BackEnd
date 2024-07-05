package com.example.tnote.boundedContext.archive.dto;

import com.example.tnote.boundedContext.classLog.dto.ClassLogSliceResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSliceResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationSliceResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingSliceResponseDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArchiveSliceResponseDto {
    private ClassLogSliceResponseDto classLogs;
    private ObservationSliceResponseDto observations;
    private ConsultationSliceResponseDto consultations;
    private ProceedingSliceResponseDto proceedings;
}
