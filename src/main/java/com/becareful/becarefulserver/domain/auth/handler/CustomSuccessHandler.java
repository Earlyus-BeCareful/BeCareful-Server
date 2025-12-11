package com.becareful.becarefulserver.domain.auth.handler;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.auth.dto.response.CustomOAuth2User;
import com.becareful.becarefulserver.domain.auth.dto.response.OAuth2LoginResponse;
import com.becareful.becarefulserver.domain.auth.dto.response.RegisteredUserLoginResponse;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.properties.CookieProperties;
import com.becareful.becarefulserver.global.properties.JwtProperties;
import com.becareful.becarefulserver.global.properties.LoginRedirectUrlProperties;
import com.becareful.becarefulserver.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomSuccessHandler.class);
    private final JwtUtil jwtUtil;
    private final CookieProperties cookieProperties;
    private final LoginRedirectUrlProperties loginRedirectUrlProperties;
    private final JwtProperties jwtProperties;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, OAuth2LoginResponse> oauth2LoginRedisTemplate;
    private final RedisTemplate<String, RegisteredUserLoginResponse> registeredUserLoginRedisTemplate;

    private final CaregiverRepository caregiverRepository;
    private final SocialWorkerRepository socialWorkerRepository;

    public CustomSuccessHandler(
            JwtUtil jwtUtil,
            CookieProperties cookieProperties,
            LoginRedirectUrlProperties loginRedirectUrlProperties,
            JwtProperties jwtProperties,
            @Qualifier("oAuth2LoginResponseRedisTemplate") RedisTemplate<String, OAuth2LoginResponse> oauth2LoginRedisTemplate,
            @Qualifier("registeredUserRedisTemplate") RedisTemplate<String, RegisteredUserLoginResponse> registeredUserLoginRedisTemplate,
            RedisTemplate<String, String> stringRedisTemplate,
            CaregiverRepository caregiverRepository,
            SocialWorkerRepository socialWorkerRepository) {
        this.jwtUtil = jwtUtil;
        this.cookieProperties = cookieProperties;
        this.loginRedirectUrlProperties = loginRedirectUrlProperties;
        this.jwtProperties = jwtProperties;
        this.oauth2LoginRedisTemplate = oauth2LoginRedisTemplate;
        this.registeredUserLoginRedisTemplate = registeredUserLoginRedisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.caregiverRepository = caregiverRepository;
        this.socialWorkerRepository = socialWorkerRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException {
        // OAuth2User
        CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();
        OAuth2LoginResponse loginInfo = oAuthUser.getLoginResponse();

        // JWT 생성용 정보만 메서드로 꺼냄
        String phoneNumber = oAuthUser.getName(); // phoneNumber

        String accessToken = jwtUtil.createAccessToken(phoneNumber);
        String refreshToken = jwtUtil.createRefreshToken(phoneNumber);

        response.addCookie(createCookie("AccessToken", accessToken, jwtProperties.getAccessTokenExpiry())); // 15분
        response.addCookie(createCookie("RefreshToken", refreshToken, jwtProperties.getRefreshTokenExpiry())); // 일주일

        String state = request.getParameter("state");
        System.out.println("successHandler >>" + state);

        String redirectUri = stringRedisTemplate.opsForValue().get("oauth2:state:" + state);
        if (redirectUri == null) {
            redirectUri = "/"; // fallback
        } else {
            stringRedisTemplate.delete("oauth2:state:" + state); // clean up
        }

        Optional<SocialWorker> socialWorkerOpt = socialWorkerRepository.findByPhoneNumber(phoneNumber);
        Optional<Caregiver> caregiverOpt = caregiverRepository.findByPhoneNumber(phoneNumber);

        if (socialWorkerOpt.isEmpty() && caregiverOpt.isEmpty()) {
            // 민감 정보는 Redis에 저장하고 key만 전달
            String guestKey = UUID.randomUUID().toString();
            oauth2LoginRedisTemplate
                    .opsForValue()
                    .set("guest:" + guestKey, loginInfo, Duration.ofMinutes(5)); // 5분 후 만료

            String redirectUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .path(loginRedirectUrlProperties.getGuestLoginRedirectUrl())
                    .queryParam("guestKey", guestKey)
                    .build()
                    .toUriString();

            response.sendRedirect(redirectUrl);
            return;
        }

        RegisteredUserLoginResponse userResponse;
        String redirectUrlPath;

        if (caregiverOpt.isPresent()) {
            Caregiver caregiver = caregiverOpt.get();
            userResponse = new RegisteredUserLoginResponse(caregiver.getName(), null);
            redirectUrlPath = loginRedirectUrlProperties.getCaregiverLoginRedirectUrl();
        } else {
            SocialWorker socialWorker = socialWorkerOpt.get();
            userResponse = new RegisteredUserLoginResponse(socialWorker.getName(), socialWorker.getNickname());
            redirectUrlPath = loginRedirectUrlProperties.getSocialWorkerLoginRedirectUrl();
        }

        String userKey = UUID.randomUUID().toString();
        registeredUserLoginRedisTemplate
                .opsForValue()
                .set("user:" + userKey, userResponse, Duration.ofMinutes(5)); // 5분 후 만료

        String redirectUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .path(redirectUrlPath)
                .queryParam("userKey", userKey)
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    private Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(cookieProperties.getCookieSecure());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", cookieProperties.getCookieSameSite());
        return cookie;
    }
}
