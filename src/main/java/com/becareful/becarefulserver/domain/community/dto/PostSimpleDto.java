package com.becareful.becarefulserver.domain.community.dto;

import com.becareful.becarefulserver.domain.nursingInstitution.vo.InstitutionRank;

public record PostSimpleDto(
        String institutionImageUrl,
        String nickname,
        InstitutionRank rank,
        boolean isImportant,
        String title,
        String createdAt,
        String thumbnailUrl
) {}
