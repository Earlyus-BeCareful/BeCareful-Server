package com.becareful.becarefulserver.global.config;

import com.becareful.becarefulserver.domain.auth.handler.CustomSuccessHandler;
import com.becareful.becarefulserver.domain.auth.service.CustomOAuth2UserService;
import com.becareful.becarefulserver.global.security.JwtAuthenticationFilter;
import com.becareful.becarefulserver.global.security.JwtExceptionHandlingFilter;
import com.becareful.becarefulserver.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final JwtUtil jwtUtil;
    private final CustomAuthorizationRequestResolver customResolver;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin((auth) -> auth.disable())
                .httpBasic((auth) -> auth.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2Login(oauth2 -> oauth2.authorizationEndpoint(
                                endpoint -> endpoint.authorizationRequestResolver(customResolver))
                        .userInfoEndpoint(config -> config.userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler))
                .authorizeHttpRequests(auth -> auth.requestMatchers(
                                "/caregiver/signup",
                                "/nursingInstitution/for-guest/**",
                                "/socialworker/signup",
                                "/socialworker/check-nickname")
                        .hasRole("GUEST")
                        .requestMatchers("/nursingInstitution/upload-profile-img")
                        .hasAnyRole("GUEST", "CENTER_DIRECTOR", "REPRESENTATIVE")
                        .requestMatchers("/caregiver/upload-profile-img")
                        .hasAnyRole("GUEST", "NONE")
                        .requestMatchers(HttpMethod.GET, "association/join-requests")
                        .hasRole("CHAIRMAN")
                        .requestMatchers(
                                "association/create",
                                "association/join-requests/*/accept",
                                "association/join-requests/*/reject",
                                "association/members/*/expel",
                                "association/upload-profile-img")
                        .hasRole("CHAIRMAN")
                        .requestMatchers(HttpMethod.POST, "association/join-requests")
                        .hasAnyRole("CENTER_DIRECTOR", "REPRESENTATIVE", "SOCIAL_WORKER")
                        .requestMatchers("association/members/overview", "association/members", "association/members/*")
                        .hasAnyRole("CHAIRMAN", "EXECUTIVE", "MEMBER")
                        .requestMatchers("/sms/**")
                        .authenticated()
                        .requestMatchers("/post")
                        .authenticated()
                        .requestMatchers("/auth/**", "/login/**", "/oauth2/**", "/favicon.ico")
                        .permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**")
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
                "https://becareful.vercel.app/",
                "https://www.carebridges.kr/",
                "https://localhost:5173",
                "https://localhost:8080",
                "https://blaybus.everdu.com",
                "https://localhost:3000"));
        configuration.addExposedHeader("Set-Cookie");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
