package com.becareful.becarefulserver.global.security;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.INVALID_REFRESH_TOKEN;

import com.becareful.becarefulserver.global.constant.SecurityConstant;
import com.becareful.becarefulserver.global.exception.ErrorMessage;
import com.becareful.becarefulserver.global.exception.exception.AuthException;
import com.becareful.becarefulserver.global.properties.CookieProperties;
import com.becareful.becarefulserver.global.properties.JwtProperties;
import com.becareful.becarefulserver.global.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final CookieProperties cookieProperties;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return isPathNeedToBeAuthenticated(path);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        String accessToken = null;
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("AccessToken")) {
                    accessToken = cookie.getValue();
                } else if (cookie.getName().equals("RefreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        // AccessToken이 만료되었는지 확인
        if (accessToken == null || !jwtUtil.isValid(accessToken)) {

            // 리프레시 토큰이 존재하고 유효할 때만 재발급 시도
            if (refreshToken != null && jwtUtil.isValid(refreshToken)) {
                // 액세스 토큰이 만료되었으면 리프레시 토큰을 사용하여 새 토큰 발급
                accessToken = getAccessTokenFromRefresh(refreshToken);

                // 새로운 액세스 토큰을 쿠키에 담아서 클라이언트에 전달
                Cookie newAccessTokenCookie = new Cookie("AccessToken", accessToken);
                newAccessTokenCookie.setMaxAge(jwtProperties.getAccessTokenExpiry());
                newAccessTokenCookie.setSecure(cookieProperties.getCookieSecure());
                newAccessTokenCookie.setPath("/");
                newAccessTokenCookie.setHttpOnly(true);
                newAccessTokenCookie.setAttribute("SameSite", cookieProperties.getCookieSameSite());

                response.addCookie(newAccessTokenCookie);
            } else if (accessToken == null && refreshToken == null) {
                log.info("URI: {}", request.getRequestURI()); // 어떤 요청에서 에러가 발생한건지 확인
                throw new AuthException(ErrorMessage.TOKEN_NOT_CONTAINED);
            } else {
                throw new AuthException(INVALID_REFRESH_TOKEN);
            }
        }
        updateSecurityContext(accessToken);
        filterChain.doFilter(request, response);
    }

    private String getAccessTokenFromRefresh(String refreshToken) {
        // Refresh Token 검증
        if (!jwtUtil.isValid(refreshToken)) {
            throw new AuthException(INVALID_REFRESH_TOKEN);
        }

        // Refresh Token에서 사용자 정보 추출
        String phoneNumber = jwtUtil.getPhoneNumber(refreshToken);

        // 새로운 Access Token 생성
        return jwtUtil.createAccessToken(phoneNumber);
    }

    private void updateSecurityContext(String accessToken) {
        String phoneNumber = jwtUtil.getPhoneNumber(accessToken);

        Authentication auth = new UsernamePasswordAuthenticationToken(phoneNumber, null, new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private boolean isPathNeedToBeAuthenticated(String path) {
        for (String checkPath : SecurityConstant.passFilterStaticUrl) {
            if (path.equals(checkPath)) {
                return true;
            }
        }

        for (String checkPath : SecurityConstant.passFilterDynamicUrl) {
            if (path.startsWith(checkPath)) {
                return true;
            }
        }

        return false;
    }
}
