package com.becareful.becarefulserver.domain.community.dto;

import com.becareful.becarefulserver.domain.socialworker.domain.vo.Rank;

public record PostSimpleDto(
        String institutionImageUrl,
        String nickname,
        Rank rank,
        boolean isImportant,
        String title,
        String createdAt,
        String thumbnailUrl
) {}
