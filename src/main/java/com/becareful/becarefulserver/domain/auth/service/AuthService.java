package com.becareful.becarefulserver.domain.auth.service;

import com.becareful.becarefulserver.global.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CookieUtil cookieUtil;

    public void logout(HttpServletResponse response) {
        response.addCookie(cookieUtil.deleteCookie("AccessToken"));
        response.addCookie(cookieUtil.deleteCookie("RefreshToken"));
        SecurityContextHolder.clearContext();
    }
}
