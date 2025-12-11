package com.becareful.becarefulserver.domain.community.dto;

import com.becareful.becarefulserver.domain.community.domain.*;

public record MediaInfoDto(String fileName, String tempKey, FileType fileType, Long fileSize) {
    public static MediaInfoDto of(String fileName, String tempKey, FileType fileType, Long fileSize) {
        return new MediaInfoDto(fileName, tempKey, fileType, fileSize);
    }
}
