package com.becareful.becarefulserver.domain.community.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.community.domain.FileType;
import com.becareful.becarefulserver.domain.community.dto.MediaInfoDto;
import com.becareful.becarefulserver.global.exception.exception.PostException;
import com.becareful.becarefulserver.global.util.FileUtil;
import java.io.IOException;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostMediaService {

    private final FileUtil fileUtil;
    private static final long MAX_IMAGE_SIZE = 30 * 1024 * 1024; // 30MB
    private static final long MAX_VIDEO_SIZE = 1024 * 1024 * 1024; // 1GB
    private static final int MAX_VIDEO_DURATION = 15 * 60; // 15분(초 단위)

    @Transactional
    public MediaInfoDto uploadPostMedia(MultipartFile file, FileType fileType, Integer videoDuration) {
        try {
            validateFileSize(file, fileType);
            if (fileType == FileType.VIDEO) {
                validateVideoDuration(videoDuration);
            }

            String fileName = generateFileName(file);
            String mediaUrl = fileUtil.upload(file, getDirectoryByFileType(fileType), fileName);

            return MediaInfoDto.of(mediaUrl, file, fileType, videoDuration);
        } catch (IOException e) {
            throw new PostException(POST_MEDIA_UPLOAD_FAILED);
        }
    }

    private void validateFileSize(MultipartFile file, FileType fileType) {
        if (fileType == FileType.IMAGE && file.getSize() <= MAX_IMAGE_SIZE) {
            return;
        }
        if (fileType == FileType.VIDEO && file.getSize() <= MAX_VIDEO_SIZE) {
            return;
        }

        throw new PostException(POST_MEDIA_VALIDATION_FAILED);
    }

    private void validateVideoDuration(Integer videoDuration) {
        if (videoDuration == null || videoDuration > MAX_VIDEO_DURATION) {
            throw new PostException(POST_MEDIA_VALIDATION_FAILED);
        }
    }

    private String getDirectoryByFileType(FileType fileType) {
        // TODO : 디렉토리 경로 상수화
        if (fileType == FileType.IMAGE) {
            return "post-images";
        }
        return "post-videos";
    }

    private String generateFileName(MultipartFile file) {
        return UUID.randomUUID() + getExtension(file);
    }

    private String getExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new PostException(POST_MEDIA_FILE_HAS_NO_NAME);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
