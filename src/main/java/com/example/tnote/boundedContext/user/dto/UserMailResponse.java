package com.example.tnote.boundedContext.user.dto;

import com.example.tnote.boundedContext.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserMailResponse {

    private Long id;
    private String email;

    public static UserMailResponse from(User user) {
        return UserMailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .build();
    }
}
