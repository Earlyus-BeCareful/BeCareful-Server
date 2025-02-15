package com.becareful.becarefulserver.global.exception;

public class ErrorMessage {

    public static final String SMS_SEND_FAILED = "SMS 전송 중 문제가 발생했습니다.";
    public static final String SMS_PHONE_NUMBER_AUTH_NOT_EXISTS = "해당 전화번호로 요청된 인증이 없습니다.";
    public static final String SMS_AUTHENTICATION_FAILED = "인증번호가 일치하지 않습니다.";

    public static final String CAREGIVER_REQUIRED_AGREEMENT = "필수 동의 항목에 동의하지 않았습니다.";
    public static final String CAREGIVER_FAILED_TO_UPLOAD_PROFILE_IMAGE = "프로필 이미지를 업로드하는 중 문제가 발생했습니다.";

    public static final String PHONE_NUMBER_NOT_EXISTS = "존재하지 않는 전화번호입니다.";
    public static final String PASSWORD_NOT_MATCH = "비밀번호가 일치하지 않습니다.";
    public static final String INVALID_TOKEN_FORMAT = "엑세스 토큰 형식이 유효하지 않습니다.";
    public static final String INVALID_TOKEN = "엑세스 토큰이 유효하지 않습니다.";
}
