package com.becareful.becarefulserver.global.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.IOException;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FileUtil {

    private final AmazonS3Client amazonS3Client;

    private static final String bucket = "becareful-s3";
    private static final String baseUrl = "https://becareful-s3.s3.ap-northeast-2.amazonaws.com/profile-image/";

    public String upload(MultipartFile file, String fileName) throws IOException {

        System.out.println(fileName);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        amazonS3Client.putObject(bucket, "profile-image/" + fileName, file.getInputStream(), metadata);
        return baseUrl + fileName;
    }
}
