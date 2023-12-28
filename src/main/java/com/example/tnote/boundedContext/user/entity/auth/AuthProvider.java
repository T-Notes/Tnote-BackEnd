package com.example.tnote.boundedContext.user.entity.auth;

import lombok.Getter;

@Getter
public enum AuthProvider {
    GOOGLE("google");
    

    private String authProvider;

    AuthProvider(String authProvider){
        this.authProvider = authProvider;
    }
}
