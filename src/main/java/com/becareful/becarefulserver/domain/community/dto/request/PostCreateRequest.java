package com.becareful.becarefulserver.domain.community.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record PostCreateRequest(
        @NotBlank(message = "글 제목은 필수 입니다.")
        String title,
        String content,
        boolean isImportant,
        List<String> imageUrls,
        List<String> videoUrls,
        List<String> fileUrls
) {}
