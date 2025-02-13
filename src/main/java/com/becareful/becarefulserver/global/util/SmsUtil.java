package com.becareful.becarefulserver.global.util;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

import java.util.Random;

@Component
public class SmsUtil {

    private DefaultMessageService messageService;

    @Value("sms.api_key")
    private String apiKey;

    @Value("sms.api_secret")
    private String apiSecret;

    @Value("sms.send_number")
    private String sendNumber;

    @PostConstruct
    public void init() {
        messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    public SingleMessageSentResponse sendMessage(String phoneNumber, String content) {
        Message message = new Message();

        message.setFrom(sendNumber);
        message.setTo(phoneNumber);
        message.setText(content);

        return messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}
