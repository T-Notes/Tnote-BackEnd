package com.example.tnote.boundedContext.classLog.dto;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
@Builder
public class ClassLogSliceResponseDto {
    List<ClassLogResponse> classLogs;

    private int numberOfClassLog;

    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;

    public static ClassLogSliceResponseDto from(List<ClassLogResponse> classLogResponseDtos,
                                                List<ClassLog> classLogList,
                                                Slice<ClassLog> allClassLogsSlice) {
        return ClassLogSliceResponseDto.builder()
                .classLogs(classLogResponseDtos)
                .numberOfClassLog(classLogList.size())
                .page(allClassLogsSlice.getPageable().getPageNumber())
                .isLast(allClassLogsSlice.isLast())
                .build();
    }
}