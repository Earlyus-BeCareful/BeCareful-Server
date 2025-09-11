package com.becareful.becarefulserver.global.util;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.FAILED_TO_CREATE_IMAGE_FILE_NAME;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
public class FileUtil {

    private final AmazonS3Client amazonS3Client;

    private static final String bucket = "care-bridges-bucket";
    private static final String baseUrl = "https://care-bridges-bucket.s3.ap-northeast-2.amazonaws.com/";

    public String upload(MultipartFile file, String directory, String fileName) throws IOException {
        String key = directory + "/" + fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3Client.putObject(bucket, key, file.getInputStream(), metadata);
        return baseUrl + key;
    }

    public String generateRandomImageFileName() {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(UUID.randomUUID().toString().getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new CaregiverException(FAILED_TO_CREATE_IMAGE_FILE_NAME);
        }
    }

    public String generateImageFileNameWithSource(String source) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(source.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new CaregiverException(FAILED_TO_CREATE_IMAGE_FILE_NAME);
        }
    }
}
