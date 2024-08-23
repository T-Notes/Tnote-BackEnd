package com.example.tnote.boundedContext.archive.dto;

import com.example.tnote.boundedContext.classLog.dto.ClassLogResponse;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.plan.dto.PlanResponse;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponse;
import com.example.tnote.boundedContext.todo.dto.TodoResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ArchiveResponse {
    private List<ClassLogResponse> classLogs;
    private List<ConsultationResponseDto> consultations;
    private List<ObservationResponseDto> observations;
    private List<ProceedingResponse> proceedings;
    private List<TodoResponseDto> todos;
    private List<PlanResponse> plans;

    public ArchiveResponse() {
    }

    public ArchiveResponse(final List<ClassLogResponse> classLogs, final List<ConsultationResponseDto> consultations,
                           final List<ObservationResponseDto> observations, final List<ProceedingResponse> proceedings,
                           final List<TodoResponseDto> todos, final List<PlanResponse> plans) {
        this.classLogs = classLogs;
        this.consultations = consultations;
        this.observations = observations;
        this.proceedings = proceedings;
        this.todos = todos;
        this.plans = plans;
    }

    public static ArchiveResponse of(final List<ClassLogResponse> classLogs,
                                     final List<ConsultationResponseDto> consultations,
                                     final List<ObservationResponseDto> observations,
                                     final List<ProceedingResponse> proceedings,
                                     final List<TodoResponseDto> todos, final List<PlanResponse> plans) {
        return new ArchiveResponse(classLogs, consultations, observations, proceedings, todos, plans);
    }

}
