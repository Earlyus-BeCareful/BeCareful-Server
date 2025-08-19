package com.becareful.becarefulserver.global.security;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.UUID;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

    private final OAuth2AuthorizationRequestResolver defaultResolver;
    private final RedisTemplate<String, String> redisTemplate;

    public CustomAuthorizationRequestResolver(
            ClientRegistrationRepository repo, RedisTemplate<String, String> redisTemplate) {
        this.defaultResolver = new DefaultOAuth2AuthorizationRequestResolver(repo, "/oauth2/authorization");
        this.redisTemplate = redisTemplate;
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
        return customize(defaultResolver.resolve(request), request);
    }

    @Override
    public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
        return customize(defaultResolver.resolve(request, clientRegistrationId), request);
    }

    private OAuth2AuthorizationRequest customize(
            OAuth2AuthorizationRequest request, HttpServletRequest servletRequest) {
        if (request == null) return null;

        String redirectUri = servletRequest.getParameter("redirectUri");
        String stateKey = UUID.randomUUID().toString();

        System.out.println("resolver>>" + stateKey);
        if (redirectUri != null) {
            redisTemplate.opsForValue().set("oauth2:state:" + stateKey, redirectUri, Duration.ofMinutes(5));
        }

        return OAuth2AuthorizationRequest.from(request).state(stateKey).build();
    }
}
