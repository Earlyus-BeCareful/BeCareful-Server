package com.becareful.becarefulserver.global.config;

import static com.becareful.becarefulserver.global.constant.UrlConstant.DEV_SERVER_URL;
import static com.becareful.becarefulserver.global.constant.UrlConstant.LOCAL_SERVER_URL;

import com.becareful.becarefulserver.domain.auth.handler.CustomSuccessHandler;
import com.becareful.becarefulserver.domain.auth.service.CustomOAuth2UserService;
import com.becareful.becarefulserver.global.security.CustomAuthorizationRequestResolver;
import com.becareful.becarefulserver.global.security.JwtAuthenticationFilter;
import com.becareful.becarefulserver.global.security.JwtExceptionHandlingFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtExceptionHandlingFilter jwtExceptionHandlingFilter;
    private final CustomAuthorizationRequestResolver customResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2.authorizationEndpoint(
                                endpoint -> endpoint.authorizationRequestResolver(customResolver))
                        .userInfoEndpoint(config -> config.userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler))
                .authorizeHttpRequests(
                        auth -> auth.requestMatchers("/auth/**", "/login/**", "/oauth2/**", "/favicon.ico")
                                .permitAll()
                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/test/**", "/ws/**")
                                .permitAll()
                                .anyRequest()
                                .authenticated())
                .exceptionHandling(
                        exception -> exception.authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("Authentication error: " + authException.getMessage());
                        }))
                .addFilterBefore(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter.class)
                .addFilterBefore(jwtExceptionHandlingFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                LOCAL_SERVER_URL,
                DEV_SERVER_URL,
                "https://becareful.vercel.app/",
                "https://www.carebridges.kr/",
                "https://localhost:5173",
                "https://localhost:3000"));

        configuration.setAllowedOriginPatterns(List.of("https://be-careful-client-*.vercel.app"));

        configuration.addExposedHeader("Set-Cookie");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
