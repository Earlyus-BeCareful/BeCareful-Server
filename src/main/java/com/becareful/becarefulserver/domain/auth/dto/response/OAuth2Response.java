package com.becareful.becarefulserver.domain.auth.dto.response;

// 카카오 리소스 서버에서 받은 정보를 서비스 레이어에 반환
public interface OAuth2Response {
    String getName();

    String getBirthyear(); // YYYY

    String getBirthday(); // MMDD

    String getGender();

    String getPhoneNumber();

    String getNickname();
}
