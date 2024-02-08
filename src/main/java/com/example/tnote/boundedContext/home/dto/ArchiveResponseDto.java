package com.example.tnote.boundedContext.home.dto;

import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSliceResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSliceResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationSliceResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingSliceResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoSliceResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArchiveResponseDto {
    private ClassLogSliceResponseDto classLogs;
    private ObservationSliceResponseDto observations;
    private ConsultationSliceResponseDto consultations;
    private ProceedingSliceResponseDto proceedings;
    private TodoSliceResponseDto todos;
}
