package com.becareful.becarefulserver.domain.community.dto;

import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;

public record AuthorSimpleDto(
        Long authorId,
        String authorName, // TODO: 닉네임으로 변경
        InstitutionRank authorInstitutionRank,
        String institutionImageUrl) {
    public static AuthorSimpleDto from(SocialWorker author) {
        return new AuthorSimpleDto(
                author.getId(),
                author.getName(),
                author.getInstitutionRank(),
                author.getNursingInstitution().getProfileImageUrl());
    }
}
