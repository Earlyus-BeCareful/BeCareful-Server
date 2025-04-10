package com.becareful.becarefulserver.global.constant;

import java.util.List;


public class SecurityConstant {
//TODO(url 수정)
    public static List<String> passFilterStaticUrl = List.of(
            "/caregiver/signup", "/caregiver/upload-profile-img",
            "/nursingInstitution/upload-profile-img",
            "/favicon.ico"
    );

    public static List<String> passFilterDynamicUrl = List.of(
            "/sms",
            "/auth",
            "/swagger-ui",
            "/v3/api-docs",
            "/oauth2",
            "/login"
    );
}
