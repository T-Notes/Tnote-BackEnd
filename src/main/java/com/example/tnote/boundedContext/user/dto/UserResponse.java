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

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getUsername())
                .school(user.getSchool())
                .subject(user.getSubject())
                .career(user.getCareer())
                .alarm(user.isAlarm())
                .build();
    }

    public static List<UserResponse> of(List<User> users) {
        return users.stream()
                .map(UserResponse::of)
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
                .build();
    }
}
