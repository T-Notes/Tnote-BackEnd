package com.example.tnote.boundedContext.schedule.dto;

import com.example.tnote.boundedContext.schedule.entity.Schedule;
import com.example.tnote.boundedContext.subject.dto.SubjectResponse;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScheduleResponse {

    private Long id;
    private String semesterName;
    private String lastClass;
    private String email; // user와 연관관계 때문 ( 중복 안되는 값 필요 )
    private List<SubjectResponse> subjects;
    private LocalDate startDate;
    private LocalDate endDate;

    public static ScheduleResponse from(Schedule schedules) {

        List<SubjectResponse> subjects = schedules.getSubjectsList().stream()
                .map(subject -> SubjectResponse.builder()
                        .id(subject.getId())
                        .subjectName(subject.getSubjectName())
                        .memo(subject.getMemo())
                        .color(subject.getColor())
                        .classTime(subject.getClassTime())
                        .classDay(subject.getClassDay().getDay())
                        .classLocation(subject.getClassLocation())
                        .build())
                .toList();

        return ScheduleResponse.builder()
                .id(schedules.getId())
                .semesterName(schedules.getSemesterName())
                .lastClass(schedules.getLastClass())
                .startDate(schedules.getStartDate())
                .endDate(schedules.getEndDate())
                .subjects(subjects)
                .email(schedules.getUser().getEmail())
                .build();
    }


    public static List<ScheduleResponse> from(List<Schedule> schedules) {
        return schedules.stream()
                .map(ScheduleResponse::from)
                .toList();
    }


    public static ScheduleResponse excludeLastDayFrom(Schedule schedule) {

        List<SubjectResponse> subjects = schedule.getSubjectsList().stream()
                .map(subject -> SubjectResponse.builder()
                        .id(subject.getId())
                        .subjectName(subject.getSubjectName())
                        .memo(subject.getMemo())
                        .color(subject.getColor())
                        .classTime(subject.getClassTime())
                        .classDay(subject.getClassDay().getDay())
                        .classLocation(subject.getClassLocation())
                        .build())
                .toList();

        return ScheduleResponse.builder()
                .id(schedule.getId())
                .email(schedule.getUser().getEmail())
                .semesterName(schedule.getSemesterName())
                .lastClass(schedule.getLastClass())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .subjects(subjects)
                .build();
    }

    public static List<ScheduleResponse> excludeLastDayFrom(List<Schedule> schedules) {
        return schedules.stream()
                .map(ScheduleResponse::excludeLastDayFrom)
                .toList();
    }
}
