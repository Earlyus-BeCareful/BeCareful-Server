package com.becareful.becarefulserver.domain.sms.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.SMS_AUTHENTICATION_FAILED;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import com.becareful.becarefulserver.global.constant.SmsConstant;
import com.becareful.becarefulserver.global.exception.SmsException;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@RedisHash(value = "smsAuthentication", timeToLive = SmsConstant.TTL)
public class SmsAuthentication {

    @Id
    private String phone;

    private String authNumber;

    @Builder(access = AccessLevel.PRIVATE)
    private SmsAuthentication(String phone, String authNumber) {
        this.phone = phone;
        this.authNumber = authNumber;
    }

    public static SmsAuthentication of(String phone, String authNumber) {
        return SmsAuthentication.builder().phone(phone).authNumber(authNumber).build();
    }

    public void authenticate(String authNumber) {
        if (!this.authNumber.equals(authNumber)) {
            throw new SmsException(SMS_AUTHENTICATION_FAILED);
        }
    }
}
