package com.becareful.becarefulserver.domain.community.dto;

import com.becareful.becarefulserver.domain.community.domain.FileType;
import com.becareful.becarefulserver.domain.community.domain.PostMedia;

public record PostMediaDto(Long id, String fileName, String mediaUrl, FileType fileType, Long fileSize) {
    public static PostMediaDto from(PostMedia postMedia) {
        return new PostMediaDto(
                postMedia.getId(),
                postMedia.getFileName(),
                postMedia.getMediaUrl(),
                postMedia.getFileType(),
                postMedia.getFileSize());
    }
}
