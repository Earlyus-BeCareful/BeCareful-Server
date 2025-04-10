package com.becareful.becarefulserver.domain.auth.dto.response;

//소셜 로그인 완료 후 프론트에 반환
public record OAuth2LoginResponse(
        String name,
        String nickname,
        String phoneNumber,
        String institutionRank,
        String associationRank,
        String birthYymmdd,
        int birthGenderCode
) {
}
