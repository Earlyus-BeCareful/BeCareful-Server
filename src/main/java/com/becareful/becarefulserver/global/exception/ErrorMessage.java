package com.becareful.becarefulserver.global.exception;

public class ErrorMessage {

    public static final String SMS_SEND_FAILED = "SMS 전송 중 문제가 발생했습니다.";
    public static final String SMS_PHONE_NUMBER_AUTH_NOT_EXISTS = "해당 전화번호로 요청된 인증이 없습니다.";
    public static final String SMS_AUTHENTICATION_FAILED = "인증번호가 일치하지 않습니다.";

    public static final String S3_MOVE_FILE_FAILED = "s3 버킷의 파일 위치를 옮기는 과정에서 문제가 발생했습니다.";
    public static final String FAILED_TO_CREATE_IMAGE_FILE_NAME = "프로필 이미지를 업로드하는 중 문제가 발생했습니다.";

    public static final String CAREGIVER_ALREADY_EXISTS = "이미 가입된 전화번호 입니다.";
    public static final String CAREGIVER_NOT_EXISTS = "해당하는 요양보호사가 없습니다.";
    public static final String CAREGIVER_REQUIRED_AGREEMENT = "필수 동의 항목에 동의하지 않았습니다.";
    public static final String CAREGIVER_WORK_APPLICATION_NOT_EXISTS = "일자리 신청서가 존재하지 않습니다.";
    public static final String CAREGIVER_CAREER_NOT_EXISTS = "요양보호사 경력사항이 존재하지 않습니다.";
    public static final String CAREGIVER_FAILED_TO_MOVE_FILE = "요양보호사 프로필 이미지를 S3에 업로드 하는 중 에러가 발생했습니다.";

    public static final String TOKEN_NOT_CONTAINED = "요청에 토큰이 포함되지 않았습니다.";
    public static final String INVALID_REFRESH_TOKEN = "리프레시 토큰이 유효하지 않습니다.";

    public static final String SOCIAL_WORKER_NOT_EXISTS = "해당하는 사회복지사가 없습니다.";
    public static final String SOCIAL_WORKER_PHONE_NUMBER_DUPLICATED = "이미 가입된 전화번호 입니다.";
    public static final String SOCIAL_WORKER_NICKNAME_DUPLICATED = "이미 존재하는 닉네임 입니다.";
    public static final String SOCIAL_WORKER_REQUIRED_AGREEMENT = "필수 약관에 동의해야 합니다.";
    public static final String SOCIAL_WORKER_NOT_DELETABLE_HAS_ASSOCIATION = "커뮤니티(협회)를 먼저 탈퇴해야 합니다.";

    public static final String NURSING_INSTITUTION_ALREADY_EXISTS = "이미 등록된 기관입니다.";
    public static final String NURSING_INSTITUTION_REQUIRED_CODE = "기관코드는 필수입니다.";
    public static final String NURSING_INSTITUTION_NOT_FOUND = "해당 ID의 요양 기관을 찾을 수 없습니다.";
    public static final String NURSING_INSTITUTION_FAILED_TO_UPLOAD_PROFILE_IMAGE =
            "요양 기관 프로필 이미지를 업로드하는 중 문제가 발생했습니다.";
    public static final String NURSING_INSTITUTION_FAILED_TO_MOVE_FILE = "요양 기관 프로필 이미지를 S3에 업로드 하는 중 에러가 발생했습니다.";

    public static final String ASSOCIATION_NOT_EXISTS = "존재하지 않는 협회입니다.";
    public static final String ASSOCIATION_MEMBERSHIP_REQUEST_NOT_EXISTS = "존재하지 않는 협회 가입 요청입니다.";
    public static final String ASSOCIATION_MEMBERSHIP_REQUEST_ALREADY_ACCEPTED =
            "가입 신청이 이미 승인되었습니다. 서비스를 이용하고 싶지 않으시다면 탈퇴해 주십시오.";
    public static final String ASSOCIATION_MEMBERSHIP_REQUEST_ALREADY_REJECTED = "가입 신청이 이미 반려되었습니다";
    public static final String ASSOCIATION_NOT_ACCESSABLE_OTHER_ASSOCIATION = "내가 속한 협회가 아니므로 접근할 수 없습니다.";
    public static final String ASSOCIATION_MEMBER_NOT_ENOUGH_RANK = "협회 회원의 권한이 충분하지 않습니다.";

    public static final String ASSOCIATION_CHAIRMAN_NOT_EXISTS = "협회장이 존재하지 않습니다.";
    public static final String ASSOCIATION_CHAIRMAN_SELECT_SUCCESSOR_FIRST = "커뮤니티 협회장 위임을 먼저 해야합니다.";
    public static final String ASSOCIATION_EXECUTIVE_SELECT_SUCCESSOR_FIRST = "커뮤니티 임원진 위임을 먼저 해야합니다.";
    public static final String ASSOCIATION_FAILED_TO_MOVE_FILE = "협회 프로필 이미지를 S3에 업로드 하는 중 에러가 발생했습니다.";

    public static final String ELDERLY_NOT_EXISTS = "존재하지 않는 어르신입니다.";
    public static final String ELDERLY_FAILED_TO_UPLOAD_PROFILE_IMAGE = "프로필 이미지를 업로드하는 중 문제가 발생했습니다.";
    public static final String ELDERLY_DIFFERENT_INSTITUTION = "다른 기관의 어르신에는 접근할 수 없습니다.";
    public static final String ELDERLY_FAILED_TO_MOVE_FILE = "어르신 프로필 이미지를 S3에 업로드 하는 중 에러가 발생했습니다.";

    public static final String RECRUITMENT_NOT_EXISTS = "매칭 공고가 존재하지 않습니다.";
    public static final String RECRUITMENT_NOT_COMPLETABLE_NOT_RECRUITING = "모집중이 아닌 공고는 완료처리할 수 없습니다.";
    public static final String RECRUITMENT_WORK_TIME_DUPLICATED = "근무 시간이 겹치는 공고가 존재합니다.";
    public static final String RECRUITMENT_DIFFERENT_INSTITUTION = "다른 기관의 공고에는 접근할 수 없습니다.";
    public static final String RECRUITMENT_NOT_CLOSABLE_COMPLETED = "모집 완료된 공고는 마감할 수 없습니다.";
    public static final String RECRUITMENT_NOT_CLOSABLE_ALREADY_CLOSED = "이미 마감된 공고입니다.";
    public static final String RECRUITMENT_NOT_UPDATABLE_NOT_RECRUITING = "모집중이 아닌 공고는 수정할 수 없습니다.";
    public static final String RECRUITMENT_NOT_UPDATABLE_APPLICANTS_OR_PROCESSING_CONTRACT_EXISTS =
            "지원자가 있거나, 채팅이 진행중인 공고는 수정할 수 없습니다.";
    public static final String RECRUITMENT_NOT_DELETABLE_NOT_RECRUITING = "모집중이 아닌 공고는 삭제할 수 없습니다.";
    public static final String RECRUITMENT_NOT_DELETABLE_APPLICANTS_OR_PROCESSING_CONTRACT_EXISTS =
            "지원자가 있거나, 채팅이 진행중인 공고는 삭제할 수 없습니다.";

    public static final String APPLICATION_NOT_EXISTS = "요양보호사 지원 정보가 존재하지 않습니다.";
    public static final String APPLICATION_CANNOT_PROPOSE_WHILE_PENDING = "보류 상태인 지원에는 근무 제안을 할 수 없습니다. 보류를 취소해주세요.";
    public static final String APPLICATION_CANNOT_PROPOSE_NOT_REVIEWING_APPLICATION_STATUS =
            "지원검토 중인 지원에만 근무제안을 보낼 수 있습니다.";
    public static final String APPLICATION_CANNOT_POSTPONE_ALREADY_PENDING = "이미 보류 상태입니다.";
    public static final String APPLICATION_CANNOT_RESUME_NOT_PENDING = "이미 보류 상태가 아닙니다.";
    public static final String APPLICATION_CANNOT_HIRE_NOT_PROPOSED = "근무 제안 상태의 지원 데이터만 채용완료 처리할 수 있습니다.";
    public static final String APPLICATION_CANNOT_FAIL_ALREADY_HIRE_PROCESS_FINISHED =
            "채용 과정이 확정된 지원 데이터는 채용불발 처리할 수 없습니다.";

    public static final String CONTRACT_NOT_EXISTS = "계약서가 존재하지 않습니다.";
    public static final String CONTRACT_CAREGIVER_NOT_EXISTS = "요양보호사가 존재하지 않아 계약서를 수정할 수 없습니다.";

    public static final String USER_CREATE_INVALID_GENDER_CODE = "유효하지 않은 성별 번호입니다.";

    public static final String ASSOCIATION_MEMBER_NOT_EXISTS = "협회 회원이 존재하지 않습니다.";
    public static final String ASSOCIATION_MEMBER_ALREADY_LEAVED = "이미 탈퇴한 협회 회원입니다.";

    public static final String COMMUNITY_REQUIRED_AGREEMENT_NOT_AGREED = "커뮤니티 관련 필수 동의 항목에 동의하지 않았습니다.";

    public static final String POST_BOARD_NOT_FOUND = "게시판이 존재하지 않습니다.";
    public static final String POST_BOARD_NOT_WRITABLE = "글 작성 권한이 없습니다.";
    public static final String POST_BOARD_NOT_READABLE = "글 조회 권한이 없습니다.";
    public static final String POST_BOARD_NOT_MY_ASSOCIATION = "내가 가입한 협회의 게시판이 아닙니다.";

    public static final String POST_NOT_FOUND = "게시글이 존재하지 않습니다.";
    public static final String POST_NOT_UPDATABLE = "글 작성자만 수정할 수 있습니다.";
    public static final String POST_DIFFERENT_POST_BOARD = "URL로 넘긴 board id 에 post가 없습니다.";
    public static final String POST_NOT_FOUND_IN_BOARD = "게시판에 해당 ID의 포스트가 존재하지 않습니다.";
    public static final String COMMENT_NOT_FOUND = "댓글이 존재하지 않습니다.";
    public static final String COMMENT_NOT_UPDATABLE = "댓글 작성자만 수정할 수 있습니다.";

    public static final String POST_MEDIA_FILE_HAS_NO_NAME = "미디어 파일의 이름이 없습니다.";
    public static final String POST_MEDIA_VALIDATION_FAILED = "미디어 파일 검증에 실패했습니다.";
    public static final String POST_MEDIA_UPLOAD_FAILED = "미디어 파일 업로드에 실패했습니다.";
    public static final String POST_MEDIA_IMAGE_COUNT_EXCEEDED = "이미지는 최대 100개까지 업로드할 수 있습니다.";
    public static final String POST_MEDIA_VIDEO_COUNT_EXCEEDED = "동영상은 최대 3개까지 업로드할 수 있습니다.";
    public static final String POST_MEDIA_FILE_COUNT_EXCEEDED = "파일은 최대 5개까지 업로드할 수 있습니다.";
    public static final String POST_MEDIA_FILE_SIZE_EXCEEDED = "파일은 1개당 최대 10MB까지 업로드할 수 있습니다.";
    public static final String POST_MEDIA_TOTAL_FILE_SIZE_EXCEEDED = "파일은 한 게시글당 최대 30MB까지 업로드할 수 있습니다.";
    public static final String POST_MEDIA_UNSUPPORTED_FILE_TYPE = "지원하지 않는 파일 타입입니다.";
}
