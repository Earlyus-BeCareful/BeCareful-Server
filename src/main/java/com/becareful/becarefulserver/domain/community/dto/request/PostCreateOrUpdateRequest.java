package com.becareful.becarefulserver.domain.community.dto.request;

import com.becareful.becarefulserver.domain.community.dto.MediaInfoDto;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record PostCreateOrUpdateRequest(
        @NotBlank(message = "글 제목은 필수 입니다.") String title,
        String content,
        boolean isImportant,
        String originalUrl,
        List<MediaInfoDto> imageList,
        List<MediaInfoDto> videoList,
        List<MediaInfoDto> fileList) {}
