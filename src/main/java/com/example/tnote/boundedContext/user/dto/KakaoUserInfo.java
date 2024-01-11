package com.example.tnote.boundedContext.user.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class KakaoUserInfo {

    private Long id;
    private KakaoAccount kakaoAccount;

    public String getEmail() {
        return kakaoAccount != null ? kakaoAccount.getEmail() : null;
    }

    public String getName() {
        return kakaoAccount != null ? kakaoAccount.getProfile().getNickname() : null;
    }

    @Getter
    private static class KakaoAccount {
        private String email;
        private Profile profile;

        @Getter
        private static class Profile {
            private String nickname;
            private String profileImageUrl;
        }
    }
}
