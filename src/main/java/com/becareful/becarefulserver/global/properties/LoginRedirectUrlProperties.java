package com.becareful.becarefulserver.global.properties;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
@Getter
public class LoginRedirectUrlProperties {
    @Value("${login.redirect_url.guest}")
    private String 	guestLoginRedirectUrl;

}
