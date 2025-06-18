package com.becareful.becarefulserver.domain.community.dto;

import com.becareful.becarefulserver.domain.community.domain.FileType;
import org.springframework.web.multipart.MultipartFile;

public record MediaInfoDto(String fileName, String mediaUrl, FileType fileType, Long fileSize, Integer videoDuration) {
    public static MediaInfoDto of(String mediaUrl, MultipartFile file, FileType fileType, Integer videoDuration) {
        return new MediaInfoDto(file.getOriginalFilename(), mediaUrl, fileType, file.getSize(), videoDuration);
    }
}
