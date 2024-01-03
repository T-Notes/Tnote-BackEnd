package com.example.tnote.boundedContext.user.dto;

import com.example.tnote.boundedContext.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String email;
    private String name;
    private String address;
    private String gubun;
    private String school;
    private String subject;
    private int career;
    private boolean alarm;

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getUsername())
                .address(user.getSchoolAddress())
                .gubun(user.getSchoolGubun())
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
                .schoolAddress(response.getAddress())
                .schoolGubun(response.getGubun())
                .school(response.getSchool())
                .subject(response.getSubject())
                .career(response.getCareer())
                .alarm(response.isAlarm())
                .build();
    }
}
