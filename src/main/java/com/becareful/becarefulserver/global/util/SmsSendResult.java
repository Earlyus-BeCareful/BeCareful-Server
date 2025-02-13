package com.becareful.becarefulserver.global.util;

public record SmsSendResult(
        String statusCode,
        String statusMessage
) {}
