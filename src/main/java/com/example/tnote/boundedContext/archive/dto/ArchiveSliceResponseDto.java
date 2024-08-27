package com.example.tnote.boundedContext.archive.dto;

import com.example.tnote.boundedContext.classLog.dto.ClassLogResponses;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponses;
import com.example.tnote.boundedContext.observation.dto.ObservationResponses;
import com.example.tnote.boundedContext.plan.dto.PlanResponses;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponses;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArchiveSliceResponseDto {
    private ClassLogResponses classLogs;
    private ObservationResponses observations;
    private ConsultationResponses consultations;
    private ProceedingResponses proceedings;
    private PlanResponses plans;
}
