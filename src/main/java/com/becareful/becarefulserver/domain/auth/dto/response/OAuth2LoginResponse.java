package com.becareful.becarefulserver.domain.auth.dto.response;

// 소셜 로그인 완료 후 프론트에 반환
public record OAuth2LoginResponse(
        String name, String nickname, String phoneNumber, String birthYymmdd, int birthGenderCode) {

    public static OAuth2LoginResponse from(OAuth2Response response) {
        String rawPhoneNumber = response.getPhoneNumber(); // "+82 10-1234-5678"
        String phoneNumber = rawPhoneNumber.replace("+82 ", "0"); // "010-1234-5678"

        String name = response.getName();
        String nickname = response.getNickname();

        String birthYear = response.getBirthyear(); // "2001"
        String birthday = response.getBirthday(); // "04-07"
        String birthYymmdd = birthYear.substring(2) + birthday.replace("-", ""); // 010407

        // 성별 코드 결정
        String genderStr = response.getGender(); // "male"

        int year = Integer.parseInt(birthYear);
        boolean isMale = "male".equalsIgnoreCase(genderStr);

        int birthGenderCode = isMale ? 1 : 2;
        if (year >= 2000) {
            birthGenderCode += 2;
        }

        return new OAuth2LoginResponse(name, nickname, phoneNumber, birthYymmdd, birthGenderCode);
    }
}
