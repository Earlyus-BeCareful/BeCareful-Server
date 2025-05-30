package com.becareful.becarefulserver.domain.auth.dto.response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

// "가공된" 리소스 서버 정보를 "프론트에 전달"
// CustomSuccessHandler에서 사용됨
@RequiredArgsConstructor
public class CustomOAuth2User implements OAuth2User {

    private final OAuth2LoginResponse loginResponse;

    // OAuth2LoginResponse 그대로 반환
    public OAuth2LoginResponse getLoginResponse() {
        return loginResponse;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(() -> "ROLE_" + loginResponse.institutionRank());
        authorities.add(() -> "ROLE_" + loginResponse.associationRank());
        return authorities;
    }

    // 사용자 식별 필드
    @Override
    public String getName() {
        return loginResponse.phoneNumber();
    }
}
