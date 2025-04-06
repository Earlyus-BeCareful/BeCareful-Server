package com.becareful.becarefulserver.common;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class SocialWorkerSecurityContextFactory implements WithSecurityContextFactory<WithSocialWorker> {

    @Override
    public SecurityContext createSecurityContext(WithSocialWorker annotation) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                annotation.phoneNumber(),
                "password",
                List.of()
        );
        context.setAuthentication(authentication);
        return context;
    }
}
