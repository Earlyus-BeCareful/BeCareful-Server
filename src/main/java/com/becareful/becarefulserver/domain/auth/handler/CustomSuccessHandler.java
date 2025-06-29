package com.becareful.becarefulserver.domain.auth.handler;

import com.becareful.becarefulserver.domain.auth.dto.response.CustomOAuth2User;
import com.becareful.becarefulserver.domain.auth.dto.response.OAuth2LoginResponse;
import com.becareful.becarefulserver.global.properties.CookieProperties;
import com.becareful.becarefulserver.global.properties.JwtProperties;
import com.becareful.becarefulserver.global.properties.LoginRedirectUrlProperties;
import com.becareful.becarefulserver.global.util.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

    public CustomSuccessHandler(
            JwtUtil jwtUtil,
            CookieProperties cookieProperties,
            LoginRedirectUrlProperties loginRedirectUrlProperties,
            JwtProperties jwtProperties,
            @Qualifier("oAuth2LoginResponseRedisTemplate") RedisTemplate<String, OAuth2LoginResponse> oauth2LoginRedisTemplate,
            RedisTemplate<String, String> stringRedisTemplate) {
        this.jwtUtil = jwtUtil;
        this.cookieProperties = cookieProperties;
        this.loginRedirectUrlProperties = loginRedirectUrlProperties;
        this.jwtProperties = jwtProperties;
        this.oauth2LoginRedisTemplate = oauth2LoginRedisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        // OAuth2User
        CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();
        OAuth2LoginResponse loginInfo = oAuthUser.getLoginResponse();

        // JWT 생성용 정보만 메서드로 꺼냄
        String name = oAuthUser.getName(); // phoneNumber
        List<String> roles = oAuthUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .toList();

        String accessToken = jwtUtil.createAccessToken(name, roles.get(0), roles.get(1));
        String refreshToken = jwtUtil.createRefreshToken(name, roles.get(0), roles.get(1));

        response.addCookie(createCookie("AccessToken", accessToken, jwtProperties.getAccessTokenExpiry())); // 24시간
        response.addCookie(createCookie("RefreshToken", refreshToken, jwtProperties.getRefreshTokenExpiry())); // 일주일

        String state = request.getParameter("state");
        System.out.println("successHandler>>" + state);

        String redirectUri = stringRedisTemplate.opsForValue().get("oauth2:state:" + state);
        if (redirectUri == null) {
            redirectUri = "/"; // fallback
        } else {
            stringRedisTemplate.delete("oauth2:state:" + state); // clean up
        }

        if (roles.contains("GUEST")) {
            // 민감 정보는 Redis에 저장하고 key만 전달
            String guestKey = UUID.randomUUID().toString();
            oauth2LoginRedisTemplate
                    .opsForValue()
                    .set(
                            "guest:" + guestKey, loginInfo, Duration.ofMinutes(5) // 5분 후 만료
                            );

            String redirectUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .path(loginRedirectUrlProperties.getGuestLoginRedirectUrl())
                    .queryParam("guestKey", guestKey)
                    .build()
                    .toUriString();

            response.sendRedirect(redirectUrl);
        } else {

            String redirectUrlPath = roles.get(0).equals("NONE")
                    ? loginRedirectUrlProperties.getCaregiverLoginRedirectUrl()
                    : loginRedirectUrlProperties.getSocialWorkerLoginRedirectUrl();
            String redirectUrl = UriComponentsBuilder.fromUriString(redirectUri)
                    .path(redirectUrlPath)
                    .build()
                    .toUriString();

            response.sendRedirect(redirectUrl);
        }
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
