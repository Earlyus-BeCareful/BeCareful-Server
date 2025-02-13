package com.becareful.becarefulserver.domain.sms.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.SMS_SEND_FAILED;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import net.nurigo.sdk.message.response.SingleMessageSentResponse;

import com.becareful.becarefulserver.domain.sms.dto.SmsAuthenticateRequest;
import com.becareful.becarefulserver.domain.sms.dto.SmsSendRequest;
import com.becareful.becarefulserver.global.exception.SmsException;
import com.becareful.becarefulserver.global.util.SmsUtil;

import java.util.Random;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsUtil smsUtil;

    public void sendAuthNumber(SmsSendRequest request) {
        String content = "Becareful: 인증번호는 [" + generateAuthNumber() + "] 입니다.";
        SingleMessageSentResponse response = smsUtil.sendMessage(request.phoneNumber(), content);

        System.out.println(response.getStatusCode());
        System.out.println(response.getStatusMessage());

        if (!response.getStatusCode().equals(HttpStatus.OK.toString())) {
            throw new SmsException(SMS_SEND_FAILED);
        }
    }

    public void authenticateNumber(SmsAuthenticateRequest request) {

    }

    private String generateAuthNumber() {
        String authNumber = generateRandomNumber();
        saveAuthNumberToRedis();
        return authNumber;
    }

    private String generateRandomNumber() {
        int baseNumber = 100000;

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        baseNumber += random.nextInt(899999);

        return Integer.toString(baseNumber);
    }

    private void saveAuthNumberToRedis() {

    }
}
