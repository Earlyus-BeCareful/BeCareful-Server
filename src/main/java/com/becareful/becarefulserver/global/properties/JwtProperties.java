package com.becareful.becarefulserver.global.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {

    @Value("${jwt.expiration_time.access_token}")
    private int accessTokenExpiry;

    @Value("${jwt.expiration_time.refresh_token}")
    private int refreshTokenExpiry;
}
