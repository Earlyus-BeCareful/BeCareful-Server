package com.becareful.becarefulserver.domain.community.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PostCreateRequest(
        @NotBlank(message = "글 제목은 필수 입니다.") String title, String content, boolean isImportant) {}
