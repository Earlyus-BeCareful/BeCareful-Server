package com.becareful.becarefulserver.global.security;

import static jakarta.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

import com.becareful.becarefulserver.global.exception.ErrorResponse;
import com.becareful.becarefulserver.global.exception.exception.AuthException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtExceptionHandlingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            if (e instanceof AuthException authException) {
                log.info("Auth Exception: {}", e.getMessage(), e);
                handleException(response, authException.getMessage(), SC_UNAUTHORIZED);
                return;
            }

            log.error("Unknown Exception: {}", e.getMessage(), e);
            handleException(response, e.getMessage(), SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleException(HttpServletResponse response, String message, int status) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        ErrorResponse errorResponse = new ErrorResponse(message);

        Writer writer = response.getWriter();
        writer.write(new ObjectMapper().writeValueAsString(errorResponse));
        writer.flush();
        writer.close();
    }
}
