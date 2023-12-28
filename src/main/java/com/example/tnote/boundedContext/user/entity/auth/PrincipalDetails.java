package com.example.tnote.boundedContext.user.entity.auth;

import com.example.tnote.boundedContext.user.entity.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

@RequiredArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {
    private final User user;

    public Long getId() {
        return user.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getUsername()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // 이메일로 로그인, 이메일로 jwt payload 생성
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // @AuthenticationPrincipal 사용을 위한 implement, jwt 인증에선 사용하지 않음 (email로 인증)
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
