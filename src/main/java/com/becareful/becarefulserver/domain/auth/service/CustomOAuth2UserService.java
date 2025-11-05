package com.becareful.becarefulserver.domain.auth.service;

import com.becareful.becarefulserver.domain.auth.dto.response.KakaoOAuth2Response;
import com.becareful.becarefulserver.domain.auth.dto.response.OAuth2Response;
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

    private final CustomUserService customUserService;

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

            return customUserService.createCustomUser(oAuth2Response);
        } catch (OAuth2AuthenticationException ex) {
            logger.error("Error during OAuth2 authentication", ex);
            throw ex; // 예외를 그대로 던져서 OAuth2 인증 실패를 처리하도록 할 수 있습니다.
        } catch (Exception ex) {
            logger.error("Unexpected error occurred while loading user", ex);
            throw ex;
        }
    }
}
