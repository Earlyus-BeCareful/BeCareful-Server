package com.becareful.becarefulserver.global.util;

import com.becareful.becarefulserver.global.properties.CookieProperties;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CookieUtil {

    private final CookieProperties cookieProperties;

    public Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(cookieProperties.getCookieSecure());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", cookieProperties.getCookieSameSite());
        return cookie;
    }
}
