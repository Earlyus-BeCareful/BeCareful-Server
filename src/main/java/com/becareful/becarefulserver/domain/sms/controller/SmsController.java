package com.becareful.becarefulserver.domain.sms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.becareful.becarefulserver.domain.sms.dto.SmsAuthenticateRequest;
import com.becareful.becarefulserver.domain.sms.dto.SmsSendRequest;
import com.becareful.becarefulserver.domain.sms.service.SmsService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sms")
@Tag(name = "SMS", description = "SMS 인증 관련 API 입니다.")
public class SmsController {

    private final SmsService smsService;

    @Operation(summary = "인증번호 SMS 전송", description = "6자리 인증번호를 생성하여 주어진 전화번호로 전송합니다.")
    @PostMapping("/send-auth-number")
    public ResponseEntity<Void> sendAuthNumber(@RequestBody SmsSendRequest request) {
        smsService.sendAuthNumber(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "인증번호 검증", description = "휴대폰 인증번호를 검증합니다.")
    @PostMapping("/authenticate-number")
    public ResponseEntity<Void> authenticateNumber(@RequestBody SmsAuthenticateRequest request) {
        smsService.authenticateNumber(request);
        return ResponseEntity.ok().build();
    }
}
