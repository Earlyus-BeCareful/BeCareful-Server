package com.becareful.becarefulserver.global.util;

import com.becareful.becarefulserver.global.properties.SmsProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("prod")
@Component
@RequiredArgsConstructor
public class SmsCoolSmsUtil implements SmsUtil {

    private final SmsProperties smsProperties;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        messageService = NurigoApp.INSTANCE.initialize(
                smsProperties.getApiKey(), smsProperties.getApiSecret(), "https://api.coolsms.co.kr");
    }

    @Override
    public SmsSendResult sendMessage(String phoneNumber, String content) {
        Message message = new Message();

        message.setFrom(smsProperties.getSendNumber());
        message.setTo(phoneNumber);
        message.setText(content);

        var response = messageService.sendOne(new SingleMessageSendingRequest(message));

        return SmsSendResult.from(response);
    }
}
