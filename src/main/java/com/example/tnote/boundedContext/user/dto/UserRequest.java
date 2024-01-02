package com.example.tnote.boundedContext.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequest {

    private String email;
    private String name;
    private String gubun;
    private String schoolName;
    private String subject;
    private int career;
    private boolean alarm;
}
