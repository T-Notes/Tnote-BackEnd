package com.example.tnote.boundedContext.archive.dto;

import com.example.tnote.boundedContext.classLog.dto.ClassLogResponse;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponse;
import com.example.tnote.boundedContext.observation.dto.ObservationResponse;
import com.example.tnote.boundedContext.plan.dto.PlanResponse;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponse;
import com.example.tnote.boundedContext.todo.dto.TodoResponse;
import java.util.List;
import lombok.Getter;

@Getter
public class ArchiveResponse {
    private List<ClassLogResponse> classLogs;
    private List<ConsultationResponse> consultations;
    private List<ObservationResponse> observations;
    private List<ProceedingResponse> proceedings;
    private List<TodoResponse> todos;
    private List<PlanResponse> plans;

    public ArchiveResponse() {
    }

    public ArchiveResponse(final List<ClassLogResponse> classLogs, final List<ConsultationResponse> consultations,
                           final List<ObservationResponse> observations, final List<ProceedingResponse> proceedings,
                           final List<TodoResponse> todos, final List<PlanResponse> plans) {
        this.classLogs = classLogs;
        this.consultations = consultations;
        this.observations = observations;
        this.proceedings = proceedings;
        this.todos = todos;
        this.plans = plans;
    }

    public ArchiveResponse(final List<ClassLogResponse> classLogs, final List<ConsultationResponse> consultations,
                           final List<ObservationResponse> observations, final List<ProceedingResponse> proceedings,
                           final List<PlanResponse> plans) {
        this.classLogs = classLogs;
        this.consultations = consultations;
        this.observations = observations;
        this.proceedings = proceedings;
        this.plans = plans;
    }

    public static ArchiveResponse of(final List<ClassLogResponse> classLogs,
                                     final List<ConsultationResponse> consultations,
                                     final List<ObservationResponse> observations,
                                     final List<ProceedingResponse> proceedings,
                                     final List<TodoResponse> todos, final List<PlanResponse> plans) {
        return new ArchiveResponse(classLogs, consultations, observations, proceedings, todos, plans);
    }

    public static ArchiveResponse of(final List<ClassLogResponse> classLogs,
                                     final List<ConsultationResponse> consultations,
                                     final List<ObservationResponse> observations,
                                     final List<ProceedingResponse> proceedings,
                                     final List<PlanResponse> plans) {
        return new ArchiveResponse(classLogs, consultations, observations, proceedings, plans);
    }

}
