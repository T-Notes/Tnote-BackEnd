package com.example.tnote.boundedContext.schedule.dto;

import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.subject.dto.SubjectResponseDto;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScheduleResponseDto {

    private Long id;
    private String semesterName;
    private String lastClass;
    private String email; // user와 연관관계 때문 ( 중복 안되는 값 필요 )
    private List<SubjectResponseDto> subjects;
    private LocalDate startDate;
    private LocalDate endDate;

    public static ScheduleResponseDto of(Schedule schedules) {

        List<SubjectResponseDto> subjects = schedules.getSubjectsList().stream()
                .map(subject -> SubjectResponseDto.builder()
                        .id(subject.getId())
                        .subjectName(subject.getSubjectName())
                        .memo(subject.getMemo())
                        .classTime(subject.getClassTime())
                        .classDay(subject.getClassDay())
                        .classLocation(subject.getClassLocation())
                        .build())
                .toList();

        return ScheduleResponseDto.builder()
                .id(schedules.getId())
                .semesterName(schedules.getSemesterName())
                .lastClass(schedules.getLastClass())
                .startDate(schedules.getStartDate())
                .endDate(schedules.getEndDate())
                .subjects(subjects)
                .email(schedules.getUser().getEmail())
                .build();
    }


    public static List<ScheduleResponseDto> of(List<Schedule> schedules) {
        return schedules.stream()
                .map(ScheduleResponseDto::of)
                .toList();
    }


    public static ScheduleResponseDto excludeLastDayOf(Schedule schedule) {

        return ScheduleResponseDto.builder()
                .id(schedule.getId())
                .email(schedule.getUser().getEmail())
                .semesterName(schedule.getSemesterName())
                .lastClass(schedule.getLastClass())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .build();
    }

    public static List<ScheduleResponseDto> excludeLastDayOf(List<Schedule> schedules) {
        return schedules.stream()
                .map(ScheduleResponseDto::excludeLastDayOf)
                .toList();
    }
}
