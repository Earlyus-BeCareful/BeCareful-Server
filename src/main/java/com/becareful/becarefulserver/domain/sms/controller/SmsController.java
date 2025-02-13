package com.becareful.becarefulserver.domain.sms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.becareful.becarefulserver.domain.sms.dto.SmsAuthenticateRequest;
import com.becareful.becarefulserver.domain.sms.dto.SmsSendRequest;
import com.becareful.becarefulserver.domain.sms.service.SmsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    @PostMapping("/send-auth-number")
    public ResponseEntity<Void> sendAuthNumber(@RequestBody SmsSendRequest request) {
        smsService.sendAuthNumber(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/authenticate-number")
    public ResponseEntity<Void> authenticateNumber(@RequestBody SmsAuthenticateRequest request) {
        smsService.authenticateNumber(request);
        return ResponseEntity.ok().build();
    }
}
