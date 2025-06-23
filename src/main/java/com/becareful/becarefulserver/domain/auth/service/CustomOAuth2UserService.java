package com.becareful.becarefulserver.domain.auth.service;

import com.becareful.becarefulserver.domain.auth.dto.response.CustomOAuth2User;
import com.becareful.becarefulserver.domain.auth.dto.response.KakaoOAuth2Response;
import com.becareful.becarefulserver.domain.auth.dto.response.OAuth2LoginResponse;
import com.becareful.becarefulserver.domain.auth.dto.response.OAuth2Response;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final SocialWorkerRepository socialworkerRepository;
    private final CaregiverRepository caregiverRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // 리소스 서버에서 사용자 정보를 받고 기존 회원인지 아닌지 판단
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oAuth2User = super.loadUser(userRequest); // provider에서 응답한 사용자 정보 객체
            logger.info("OAuth2User: {}", oAuth2User.getAttributes());

            String registrationId = userRequest.getClientRegistration().getRegistrationId(); // kakao인지

            OAuth2Response oAuth2Response = null;
            logger.info("Registration ID: {}", registrationId);

            if (registrationId.equals("kakao")) {
                oAuth2Response = new KakaoOAuth2Response(oAuth2User.getAttributes());
            } else {
                throw new OAuth2AuthenticationException("Unsupported OAuth2 provider: " + registrationId);
            }

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
        } catch (OAuth2AuthenticationException ex) {
            logger.error("Error during OAuth2 authentication", ex);
            throw ex; // 예외를 그대로 던져서 OAuth2 인증 실패를 처리하도록 할 수 있습니다.
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while loading user", ex);
            throw ex;
        }
    }
}
