package com.becareful.becarefulserver.global.util;

import jakarta.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Profile("prod")
@Component
public class SmsCoolSmsUtil implements SmsUtil {

    private DefaultMessageService messageService;

    @Value("${sms.api_key}")
    private String apiKey;

    @Value("${sms.api_secret}")
    private String apiSecret;

    @Value("${sms.send_number}")
    private String sendNumber;

    @PostConstruct
    public void init() {
        messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    @Override
    public SmsSendResult sendMessage(String phoneNumber, String content) {
        Message message = new Message();

        message.setFrom(sendNumber);
        message.setTo(phoneNumber);
        message.setText(content);

        var response = messageService.sendOne(new SingleMessageSendingRequest(message));

        return new SmsSendResult(response.getStatusCode(), response.getStatusMessage());
    }
}
