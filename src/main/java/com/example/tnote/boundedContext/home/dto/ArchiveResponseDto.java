package com.example.tnote.boundedContext.home.dto;

import com.example.tnote.boundedContext.classLog.dto.ClassLogResponseDto;
import com.example.tnote.boundedContext.classLog.dto.ClassLogSliceResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationResponseDto;
import com.example.tnote.boundedContext.consultation.dto.ConsultationSliceResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationResponseDto;
import com.example.tnote.boundedContext.observation.dto.ObservationSliceResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingResponseDto;
import com.example.tnote.boundedContext.proceeding.dto.ProceedingSliceResponseDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ArchiveResponseDto {
    private List<ClassLogSliceResponseDto> classLogs;
    private List<ObservationSliceResponseDto> observations;
    private List<ConsultationSliceResponseDto> consultations;
    private List<ProceedingSliceResponseDto> proceedings;

    public static ArchiveResponseDto of(List<ClassLogSliceResponseDto> classLogs, List<ObservationSliceResponseDto> observations,
                                 List<ConsultationSliceResponseDto> consultations, List<ProceedingSliceResponseDto> proceedings) {
        return ArchiveResponseDto.builder()
                .classLogs(classLogs)
                .consultations(consultations)
                .observations(observations)
                .proceedings(proceedings)
                .build();
    }
}
