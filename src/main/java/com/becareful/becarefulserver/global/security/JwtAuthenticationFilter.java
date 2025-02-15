package com.becareful.becarefulserver.global.security;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.INVALID_TOKEN_FORMAT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.becareful.becarefulserver.global.exception.exception.AuthException;
import com.becareful.becarefulserver.global.util.JwtUtil;

import java.io.IOException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (StringUtils.hasText(request.getHeader(HttpHeaders.AUTHORIZATION))) {
            String token = getToken(request);
            jwtUtil.validateToken(token);
            var auth = new UsernamePasswordAuthenticationToken(
                    jwtUtil.getPhoneNumber(token),
                    null
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (StringUtils.hasText(token)) {
            return token;
        }

        throw new AuthException(INVALID_TOKEN_FORMAT);
    }
}
