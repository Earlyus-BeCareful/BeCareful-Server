package com.becareful.becarefulserver.global.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class LoginRedirectUrlProperties {
    @Value("${login.redirect_url.guest}")
    private String guestLoginRedirectUrl;

    @Value("${login.redirect_url.social_worker}")
    private String socialWorkerLoginRedirectUrl;

    @Value("${login.redirect_url.caregiver}")
    private String caregiverLoginRedirectUrl;
}
