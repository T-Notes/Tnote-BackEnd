package com.example.tnote.boundedContext.schedule.dto;

import com.example.tnote.boundedContext.schedule.entity.ClassDay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SubjectRequestDto {

    private String subjectName;
    private String classTime;
    private ClassDay classDay;
    private String classLocation;
    private String memo;
    private Long scheduleId;  // 연관관계를 위함.
}
