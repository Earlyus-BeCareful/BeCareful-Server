package com.becareful.becarefulserver.global.util;

public interface SmsUtil {

    SmsSendResult sendMessage(String phoneNumber, String message);
}
