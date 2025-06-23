package com.becareful.becarefulserver.global.constant;

import java.util.List;

public class SecurityConstant {
    // TODO(url 수정)
    public static List<String> passFilterStaticUrl = List.of("/favicon.ico");

    public static List<String> passFilterDynamicUrl =
            List.of("/sms", "/auth", "/swagger-ui", "/v3/api-docs", "/oauth2", "/login");
}
