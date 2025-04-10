package com.becareful.becarefulserver.domain.auth.dto.response;

import java.util.Map;


//카카오 리소스 서버에서 받은 정보를 서비스 레이어에 반환
public class KakaoOAuth2Response implements OAuth2Response {
    private final Map<String, Object> attributes;

    public KakaoOAuth2Response(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    private Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) attributes.get("kakao_account");
    }

    private Map<String, Object> getKakaoProfile() {
        return (Map<String, Object>) getKakaoAccount().get("profile");
    }

    @Override
    public String getPhoneNumber() {
        return (String) getKakaoAccount().get("phone_number");  // "+82 10-1234-5678"
    }
    @Override
    public String getName() {
        return (String) getKakaoAccount().get("name");
    }
    @Override
    public String getNickname() {
        return (String) getKakaoProfile().get("nickname");
    }

    @Override
    public String getBirthyear() {
        return (String) getKakaoAccount().get("birthyear");     // "2001"
    }
    @Override
    public String getBirthday() {
        return (String) getKakaoAccount().get("birthday");      // "04-07"
    }
    @Override
    public String getGender() {
        return (String) getKakaoAccount().get("gender");        // "male" or "female"
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }
}