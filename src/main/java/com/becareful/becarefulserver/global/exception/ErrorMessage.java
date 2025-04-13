package com.becareful.becarefulserver.global.exception;

public class ErrorMessage {

    public static final String SMS_SEND_FAILED = "SMS 전송 중 문제가 발생했습니다.";
    public static final String SMS_PHONE_NUMBER_AUTH_NOT_EXISTS = "해당 전화번호로 요청된 인증이 없습니다.";
    public static final String SMS_AUTHENTICATION_FAILED = "인증번호가 일치하지 않습니다.";

    public static final String CAREGIVER_ALREADY_EXISTS = "이미 가입된 전화번호 입니다.";
    public static final String CAREGIVER_NOT_EXISTS_WITH_PHONE_NUMBER = "해당 전화번호를 갖는 요양보호사가 없습니다.";
    public static final String CAREGIVER_REQUIRED_AGREEMENT = "필수 동의 항목에 동의하지 않았습니다.";
    public static final String CAREGIVER_FAILED_TO_UPLOAD_PROFILE_IMAGE = "프로필 이미지를 업로드하는 중 문제가 발생했습니다.";
    public static final String CAREGIVER_WORK_APPLICATION_NOT_EXISTS = "일자리 신청서가 존재하지 않습니다.";

    public static final String PHONE_NUMBER_NOT_EXISTS = "존재하지 않는 전화번호입니다.";
    public static final String PASSWORD_NOT_MATCH = "비밀번호가 일치하지 않습니다.";
    public static final String INVALID_TOKEN_FORMAT = "엑세스 토큰 형식이 유효하지 않습니다.";
    public static final String INVALID_TOKEN = "엑세스 토큰이 유효하지 않습니다.";
    public static final String TOKEN_NOT_CONTAINED = "요청에 토큰이 포함되지 않았습니다.";
    public static String INVALID_REFRESH_TOKEN = "리프레시 토튼이 유효하지 않습니다.";

    public static final String SOCIALWORKER_REQUIRED_AGREEMENT = "필수 약관에 동의해야 합니다.";
    public static final String SOCIALWORKER_ALREADY_EXISTS_PHONENUMBER = "이미 가입된 전화번호 입니다.";
    public static final String SOCIAlWORKER_ALREADY_EXISTS_NICKNAME = "이미 존재하는 닉네임 입니다.";
    public static final String SOCIALWORKER_NOT_EXISTS = "해당하는 사회복지사가 없습니다.";
    public static final String  NURSING_INSTITUTION_REQUIRE_CODE = "기관코드는 필수입니다.";

    public static final String NURSING_INSTITUTION_ALREADY_EXISTS = "이미 등록된 기관입니다.";
    public static final String NURSING_INSTITUTION_NOT_FOUND = "해당 ID의 요양 기관을 찾을 수 없습니다.";
    public static final String NURSING_INSTITUTION_FAILED_TO_UPLOAD_PROFILE_IMAGE = "프로필 이미지를 업로드하는 중 문제가 발생했습니다.";

    public static final String ASSOCIATION_INSTITUTION_NOT_EXISTS = "존재하지 않는 협회입니다.";

    public static final String ELDERLY_FAILED_TO_UPLOAD_PROFILE_IMAGE = "프로필 이미지를 업로드하는 중 문제가 발생했습니다.";
    public static final String ELDERLY_NOT_EXISTS = "존재하지 않는 어르신입니다.";

    public static final String RECRUITMENT_NOT_EXISTS = "매칭 공고가 존재하지 않습니다.";
    public static final String MATCHING_NOT_EXISTS = "매칭 또는 지원 정보가 존재하지 않습니다.";
    public static final String MATCHING_CANNOT_APPLY = "미지원 공고에만 지원할 수 있습니다.";
    public static final String MATCHING_CANNOT_REJECT = "미지원 공고만 거절할 수 있습니다.";

    public static final String USER_CREATE_INVALID_GENDER_CODE = "유효하지 않은 성별 번호입니다.";

    public static final String POST_BOARD_NOT_FOUND = "게시판이 존재하지 않습니다.";
    public static final String POST_BOARD_NOT_WRITABLE = "글 작성 권한이 없습니다.";
    public static final String POST_BOARD_NOT_READABLE = "글 조회 권한이 없습니다.";

    public static final String POST_NOT_FOUND = "게시글이 존재하지 않습니다.";
    public static final String POST_NOT_UPDATABLE = "글 작성자만 수정할 수 있습니다.";
    public static final String POST_DIFFERENT_POST_BOARD = "URL로 넘긴 board id 에 post가 없습니다.";
}
