package com.becareful.becarefulserver.domain.chat;

import com.becareful.becarefulserver.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.util.WebUtils;

import java.util.Map;

@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandlerInterceptor {
    private final JwtUtil jwtUtil;
    private final

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler wsHandler,
                                   Map<String, Object> attributes) throws Exception{
        if (request instanceof ServletServerHttpRequest serverHttpRequest) {
            HttpServletRequest httpServletRequest = serverHttpRequest.getServletRequest();
            Cookie cookie = WebUtils.getCookie(httpServletRequest, "JWT");
            if(cookie != null && jwtUtil.isValid(cookie.getValue())) {
                Authentication auth = new ;
            }
        }

    }
}
