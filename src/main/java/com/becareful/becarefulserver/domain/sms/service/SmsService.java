package com.becareful.becarefulserver.domain.sms.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.SMS_PHONE_NUMBER_AUTH_NOT_EXISTS;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.SMS_SEND_FAILED;

import org.springframework.stereotype.Service;

import com.becareful.becarefulserver.domain.sms.domain.SmsAuthentication;
import com.becareful.becarefulserver.domain.sms.dto.SmsAuthenticateRequest;
import com.becareful.becarefulserver.domain.sms.dto.SmsSendRequest;
import com.becareful.becarefulserver.domain.sms.repository.SmsRepository;
import com.becareful.becarefulserver.global.exception.SmsException;
import com.becareful.becarefulserver.global.util.SmsSendResult;
import com.becareful.becarefulserver.global.util.SmsUtil;

import java.util.Random;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SmsService {

    private final SmsUtil smsUtil;
    private final SmsRepository smsRepository;

    public void sendAuthNumber(SmsSendRequest request) {
        // TODO : Redis Transaction 설정

        // TODO : 인증번호 유효기간 TTL 설정
        // TODO : status code API 명세에 맞게 설정
        String authNumber = generateAuthNumber();
        String content = "Becareful: 인증번호는 [" + authNumber + "] 입니다.";
        SmsSendResult result = smsUtil.sendMessage(request.phoneNumber(), content);

        smsRepository.save(SmsAuthentication.of(request.phoneNumber(), authNumber));

        System.out.println(result.statusCode());
        System.out.println(result.statusMessage());
        System.out.println(authNumber);

        if (!result.statusCode().equals("200")) {
            throw new SmsException(SMS_SEND_FAILED);
        }
    }

    public void authenticateNumber(SmsAuthenticateRequest request) {
        SmsAuthentication auth = smsRepository.findById(request.phoneNumber())
                .orElseThrow(() -> new SmsException(SMS_PHONE_NUMBER_AUTH_NOT_EXISTS));

        auth.authenticate(request.authNumber());
    }

    private String generateAuthNumber() {
        int baseNumber = 100000;

        Random random = new Random();
        random.setSeed(System.currentTimeMillis());
        baseNumber += random.nextInt(899999);

        return Integer.toString(baseNumber);
    }
}
