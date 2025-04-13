package com.becareful.becarefulserver.domain.community.dto.request;

import java.util.List;

public record PostUpdateRequest(
        String title,
        String content,
        boolean isImportant,
        List<String> fileUrls,
        List<String> imageUrls,
        List<String> videoUrls
) {}
