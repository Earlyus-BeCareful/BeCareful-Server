package com.becareful.becarefulserver.global.util;

import com.becareful.becarefulserver.global.exception.exception.S3Exception;
import com.becareful.becarefulserver.global.properties.*;
import java.nio.charset.*;
import java.security.*;
import java.util.*;
import java.util.stream.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;

@Slf4j
@Component
public class S3Util {
    private final S3Client s3Client;
    private final String baseUrl;
    private final String bucket;

    public S3Util(S3Client s3Client, S3Properties s3Properties) {
        this.s3Client = s3Client;
        this.baseUrl = s3Properties.getBaseUrl();
        this.bucket = s3Properties.getBucket();
    }

    public String getTempKey(String directory, String fileName) {
        return directory + "/temp/" + fileName;
    }

    public String getPermanentKeyFromTempKey(String tempKey) {
        String[] parts = tempKey.split("/");
        String directory = parts[0];
        if (parts.length == 4) {
            directory += "/" + parts[1];
        } // post 디렉토리인 경우
        String fileName = parts[parts.length - 1];
        return directory + "/permanent/" + fileName;
    }

    public String getPermanentUrlFromTempKey(String tempKey) {
        return baseUrl + getPermanentKeyFromTempKey(tempKey);
    }

    public String generateImageFileNameWithSource(String source) {
        try {
            String uniqueSource = source + UUID.randomUUID();
            var md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(uniqueSource.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to create image file name", e);
        }
    }

    public List<String> listFiles(String prefix) {
        ListObjectsV2Request request =
                ListObjectsV2Request.builder().bucket(bucket).prefix(prefix).build();

        return s3Client.listObjectsV2(request).contents().stream()
                .map(S3Object::key)
                .filter(key -> !key.endsWith("/"))
                .collect(Collectors.toList());
    }

    public void deleteFile(String key) {
        try {
            s3Client.deleteObject(
                    DeleteObjectRequest.builder().bucket(bucket).key(key).build());
        } catch (S3Exception e) {
            log.warn("[S3Util] 파일 삭제 실패: {}", key, e);
        }
    }

    public String getUrlFromKey(String key) {
        return baseUrl + key;
    }
}
