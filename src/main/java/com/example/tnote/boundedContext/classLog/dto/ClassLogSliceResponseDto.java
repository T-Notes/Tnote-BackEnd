package com.example.tnote.boundedContext.classLog.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClassLogSliceResponseDto {
    List<ClassLogResponseDto> classLogs;

    private int numberOfClassLog;

    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;
}
