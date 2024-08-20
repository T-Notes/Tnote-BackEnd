package com.example.tnote.boundedContext.classLog.dto;

import com.example.tnote.boundedContext.classLog.entity.ClassLog;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Slice;

@Getter
@Builder
public class ClassLogResponses {
    private List<ClassLogResponse> classLogs;

    private int numberOfClassLog;

    private long page;
    @JsonProperty(value = "isLast")
    private Boolean isLast;

    public ClassLogResponses() {
    }

    public ClassLogResponses(final List<ClassLogResponse> classLogs, final int numberOfClassLog, final long page,
                             final Boolean isLast) {
        this.classLogs = classLogs;
        this.numberOfClassLog = numberOfClassLog;
        this.page = page;
        this.isLast = isLast;
    }

    public static ClassLogResponses of(final List<ClassLogResponse> classLogResponses,
                                       final List<ClassLog> classLogList,
                                       final Slice<ClassLog> allClassLogsSlice) {
        return ClassLogResponses.builder()
                .classLogs(classLogResponses)
                .numberOfClassLog(classLogList.size())
                .page(allClassLogsSlice.getPageable().getPageNumber())
                .isLast(allClassLogsSlice.isLast())
                .build();
    }
}