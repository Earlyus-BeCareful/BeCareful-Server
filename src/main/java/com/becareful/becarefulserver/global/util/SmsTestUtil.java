package com.becareful.becarefulserver.global.util;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile({"local", "test"})
@Component
public class SmsTestUtil implements SmsUtil {

    @Override
    public SmsSendResult sendMessage(String phoneNumber, String message) {
        return new SmsSendResult("2000", "전송 성공");
    }
}
