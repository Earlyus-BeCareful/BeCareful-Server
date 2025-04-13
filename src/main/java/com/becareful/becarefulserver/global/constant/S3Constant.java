package com.becareful.becarefulserver.global.constant;

public class S3Constant {

    public static final String CAREGIVER_DEFAULT_PROFILE_IMAGE_URL = "https://becareful-s3.s3.ap-northeast-2.amazonaws.com/profile-image/caregiver_default.png";
    public static final String INSTITUTION_DEFAULT_PROFILE_IMAGE_URL = "https://becareful-s3.s3.ap-northeast-2.amazonaws.com/profile-image/intitution_default.png";
    public static final String ELDERLY_DEFAULT_PROFILE_IMAGE_URL = "https://becareful-s3.s3.ap-northeast-2.amazonaws.com/profile-image/elderly_default.png";

    public static final String PROFILE_IMAGE_PATH = "profile-image/";
    public static final String POST_IMAGE_PATH = "post-image/";
    public static final String POST_FILE_PATH = "post-file/";
    public static final String POST_VIDEO_PATH = "post-video/";

    public static final Long IMAGE_MAX_SIZE = 30 * 1024 * 1024L;
    public static final Long VIDEO_MAX_SIZE = 1 * 1024 * 1024 * 1024L;
    public static final Long FILE_MAX_SIZE = 10 * 1024 * 1024L;
}