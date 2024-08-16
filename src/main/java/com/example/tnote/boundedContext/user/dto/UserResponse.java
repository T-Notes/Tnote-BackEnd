package com.example.tnote.boundedContext.user.dto;

import com.example.tnote.boundedContext.user.entity.User;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponse {

    private Long id;
    private String email;
    private String name;
    private String school;
    private String subject;
    private int career;
    private boolean alarm;
    private int scheduleId;
    private String semesterName;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getUsername())
                .school(user.getSchool())
                .subject(user.getSubject())
                .career(user.getCareer())
                .alarm(user.isAlarm())
                .scheduleId(user.getLastScheduleId())
                .semesterName(user.getLastScheduleName())
                .build();
    }

    public static List<UserResponse> from(List<User> users) {
        return users.stream()
                .map(UserResponse::from)
                .toList();
    }

    public static User toEntity(UserResponse response) {
        return User.builder()
                .id(response.getId())
                .email(response.getEmail())
                .username(response.getName())
                .school(response.getSchool())
                .subject(response.getSubject())
                .career(response.getCareer())
                .alarm(response.isAlarm())
                .lastScheduleId(response.getScheduleId())
                .lastScheduleName(response.getSemesterName())
                .build();
    }
}
