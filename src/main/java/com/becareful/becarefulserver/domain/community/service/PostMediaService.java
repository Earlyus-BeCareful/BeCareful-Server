package com.becareful.becarefulserver.domain.community.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.common.dto.response.*;
import com.becareful.becarefulserver.domain.community.domain.*;
import com.becareful.becarefulserver.domain.community.dto.request.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import com.becareful.becarefulserver.global.service.*;
import com.becareful.becarefulserver.global.util.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostMediaService {

    private static final long MAX_IMAGE_SIZE = 30 * 1024 * 1024; // 30MB
    private static final long MAX_VIDEO_SIZE = 1024 * 1024 * 1024; // 1GB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_VIDEO_DURATION = 15 * 60; // 15분(초 단위)
    private final S3Util s3Util;
    private final S3Service s3Service;

    private void validateFileSize(int fileSize, FileType fileType) {
        if (fileType == FileType.IMAGE && fileSize <= MAX_IMAGE_SIZE) {
            return;
        }
        if (fileType == FileType.VIDEO && fileSize <= MAX_VIDEO_SIZE) {
            return;
        }
        if (fileType == FileType.FILE && fileSize <= MAX_FILE_SIZE) {
            return;
        }
        throw new PostException(POST_MEDIA_VALIDATION_FAILED);
    }

    private void validateVideoDuration(Integer videoDuration) {
        if (videoDuration == null || videoDuration > MAX_VIDEO_DURATION) {
            throw new PostException(POST_MEDIA_VALIDATION_FAILED);
        }
    }

    private java.lang.String getDirectoryByFileType(FileType fileType) {
        // TODO : 디렉토리 경로 상수화
        if (fileType == FileType.IMAGE) {
            return "post/images";
        }
        if (fileType == FileType.VIDEO) {
            return "post/videos";
        }
        if (fileType == FileType.FILE) {
            return "post/files";
        }
        throw new PostException(POST_MEDIA_UNSUPPORTED_FILE_TYPE);
    }

    public PresignedUrlResponse getPresignedUrl(PostMediaPresignedUrlRequest request) {

        try {
            validateFileSize(request.fileSize(), request.fileType());
            if (request.fileType() == FileType.VIDEO) {
                validateVideoDuration(request.videoDuration());
            }

            String fileName = s3Util.generateImageFileNameWithSource(request.fileName());
            return s3Service.createPresignedUrl(
                    getDirectoryByFileType(request.fileType()), fileName, request.contentType());

        } catch (Exception e) {
            throw new PostException(POST_MEDIA_UPLOAD_FAILED);
        }
    }
}
