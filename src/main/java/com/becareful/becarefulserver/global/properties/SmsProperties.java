package com.becareful.becarefulserver.global.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SmsProperties {

    @Value("${sms.api_key}")
    private String apiKey;

    @Value("${sms.api_secret}")
    private String apiSecret;

    @Value("${sms.send_number}")
    private String sendNumber;
}
