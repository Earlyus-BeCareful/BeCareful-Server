package com.becareful.becarefulserver.global.util;

import net.nurigo.sdk.message.response.SingleMessageSentResponse;

public record SmsSendResult(String statusCode, String statusMessage) {

    public static SmsSendResult from(SingleMessageSentResponse response) {
        return new SmsSendResult(response.getStatusCode(), response.getStatusMessage());
    }

    public boolean isSuccessful() {
        return statusCode.equals("2000") | statusCode.equals("3000") | statusCode.equals("4000");
    }
}
