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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomSuccessHandler.class);
    private final JwtUtil jwtUtil;
    private final CookieProperties cookieProperties;
    private final LoginRedirectUrlProperties  loginRedirectUrlProperties;
    private final JwtProperties jwtProperties;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        //OAuth2User
        CustomOAuth2User oAuthUser = (CustomOAuth2User) authentication.getPrincipal();
        OAuth2LoginResponse loginInfo = oAuthUser.getLoginResponse();

        // JWT 생성용 정보만 메서드로 꺼냄
        String name = oAuthUser.getName(); //phoneNumber
        List<String> roles = oAuthUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(role -> role.replace("ROLE_", ""))
                .toList();

        String accessToken = jwtUtil.createAccessToken(name, roles.get(0), roles.get(1));
        String refreshToken = jwtUtil.createRefreshToken(name, roles.get(0), roles.get(1));

        response.addCookie(createCookie("AccessToken", accessToken, jwtProperties.getAccessTokenExpiry())); //3시간
        response.addCookie(createCookie("RefreshToken", refreshToken, jwtProperties.getRefreshTokenExpiry())); //일주일

        if(roles.contains("GUEST")) {//비회원
            String encodedName = URLEncoder.encode(loginInfo.name(), StandardCharsets.UTF_8);
            String encodedNickname = URLEncoder.encode(loginInfo.nickname(), StandardCharsets.UTF_8);

            response.addCookie(createReadableCookie("Name", encodedName));
            response.addCookie(createReadableCookie("Nickname", encodedNickname));
            response.addCookie(createReadableCookie("PhoneNumber", loginInfo.phoneNumber()));
            response.addCookie(createReadableCookie("BirthYymmdd", String.valueOf(loginInfo.birthYymmdd())));
            response.addCookie(createReadableCookie("BirthGenderCode", String.valueOf(loginInfo.birthGenderCode())));

            //TODO(회원 로그인 리다이렉트 주소 설정)
            response.sendRedirect(loginRedirectUrlProperties.getGuestLoginRedirectUrl());
        } else if (roles.contains("NONE")) { //기관 사회복지사 & 요양 보호사
            response.sendRedirect(loginRedirectUrlProperties.getGuestLoginRedirectUrl());
        }
        else{ //협회 회원
            response.sendRedirect(loginRedirectUrlProperties.getGuestLoginRedirectUrl());
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
    private Cookie createReadableCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60*60*5);
        cookie.setSecure(cookieProperties.getCookieSecure());
        cookie.setPath("/");
        cookie.setHttpOnly(false); //JS에서 접근 가능하게 설정
        cookie.setAttribute("SameSite", cookieProperties.getCookieSameSite());
        return cookie;
    }
}