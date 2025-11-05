package com.becareful.becarefulserver.domain.auth.service;

import com.becareful.becarefulserver.domain.auth.dto.response.CustomOAuth2User;
import com.becareful.becarefulserver.domain.auth.dto.response.OAuth2LoginResponse;
import com.becareful.becarefulserver.domain.auth.dto.response.OAuth2Response;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomUserService {

    private final SocialWorkerRepository socialworkerRepository;
    private final CaregiverRepository caregiverRepository;

    @Transactional(readOnly = true)
    public OAuth2User createCustomUser(OAuth2Response oAuth2Response) {
        String rawPhoneNumber = oAuth2Response.getPhoneNumber(); // "+82 10-1234-5678"
        String phoneNumber = rawPhoneNumber.replace("+82 ", "0"); // "010-1234-5678"

        String name = oAuth2Response.getName();
        String nickname = oAuth2Response.getNickname();

        String birthYear = oAuth2Response.getBirthyear(); // "2001"
        String birthday = oAuth2Response.getBirthday(); // "04-07"
        String birthYymmdd = birthYear.substring(2) + birthday.replace("-", ""); // 010407

        // 성별 코드 결정
        String genderStr = oAuth2Response.getGender(); // "male"

        int year = Integer.parseInt(birthYear);
        boolean isMale = "male".equalsIgnoreCase(genderStr);

        int birthGenderCode;
        if (year >= 2000) {
            birthGenderCode = isMale ? 3 : 4;
        } else {
            birthGenderCode = isMale ? 1 : 2;
        }

        // TODO(role 수정)
        Optional<SocialWorker> socialWorker = socialworkerRepository.findByPhoneNumber(phoneNumber);
        Optional<Caregiver> caregiver = caregiverRepository.findByPhoneNumber(phoneNumber);

        boolean isGuest = socialWorker.isEmpty() && caregiver.isEmpty();

        String institutionRank = isGuest
                ? "GUEST"
                : socialWorker.map(sw -> sw.getInstitutionRank().toString()).orElse("NONE");

        String associationRank = isGuest
                ? "GUEST"
                : socialWorker.map(sw -> sw.getAssociationRank().name()).orElse("NONE");

        OAuth2LoginResponse loginResponse = new OAuth2LoginResponse(
                name, nickname, phoneNumber, institutionRank, associationRank, birthYymmdd, birthGenderCode);

        return new CustomOAuth2User(loginResponse);
    }
}
