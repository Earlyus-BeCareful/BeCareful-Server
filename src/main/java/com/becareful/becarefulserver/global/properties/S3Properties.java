package com.becareful.becarefulserver.global.properties;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.*;

@Getter
@Component
public class S3Properties {

    @Value("${aws.s3.base-url}")
    private String baseUrl;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;
}
