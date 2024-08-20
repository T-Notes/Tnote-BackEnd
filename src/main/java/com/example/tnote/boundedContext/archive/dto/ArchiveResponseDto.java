package com.example.tnote.boundedContext.archive.dto;

import com.example.tnote.boundedContext.classLog.dto.ClassLogResponse;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.todo.dto.TodoResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArchiveResponseDto {
    private List<ClassLogResponse> classLogs;
    private List<ConsultationResponseDto> consultations;
    private List<ObservationResponseDto> observations;
    private List<ProceedingResponseDto> proceedings;
    private List<TodoResponseDto> todos;
}
