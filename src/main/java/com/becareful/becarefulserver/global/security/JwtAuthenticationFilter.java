package com.becareful.becarefulserver.global.security;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.INVALID_TOKEN_FORMAT;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.TOKEN_NOT_CONTAINED;

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

import com.becareful.becarefulserver.global.constant.SecurityConstant;
import com.becareful.becarefulserver.global.exception.exception.AuthException;
import com.becareful.becarefulserver.global.util.JwtUtil;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return isPathNeedToBeAuthenticated(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = getToken(request);
        jwtUtil.validateToken(token);
        var auth = new UsernamePasswordAuthenticationToken(
                jwtUtil.getPhoneNumber(token),
                null,
                List.of()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (StringUtils.hasText(request.getHeader(HttpHeaders.AUTHORIZATION))) {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            if (StringUtils.hasText(token)) {
                return token;
            }

            throw new AuthException(INVALID_TOKEN_FORMAT);
        }

        throw new AuthException(TOKEN_NOT_CONTAINED);
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
