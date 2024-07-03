package com.example.tnote.boundedContext.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LogsDeleteResponseDto {
    private int deletedClassLogsCount;
    private int deletedProceedingsCount;
    private int deletedObservationsCount;
    private int deletedConsultationsCount;

    public static LogsDeleteResponseDto of(int deletedClassLogsCount, int deletedProceedingsCount,
                                           int deletedObservationsCount, int deletedConsultationsCount) {
        return new LogsDeleteResponseDto(deletedClassLogsCount, deletedProceedingsCount, deletedObservationsCount,
                deletedConsultationsCount);
    }
}
