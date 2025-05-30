package com.becareful.becarefulserver.domain.sms.dto;

public record SmsAuthenticateRequest(String phoneNumber, String authNumber) {}
