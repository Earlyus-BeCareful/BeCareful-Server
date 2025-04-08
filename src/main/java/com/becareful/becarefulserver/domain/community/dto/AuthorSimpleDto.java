package com.becareful.becarefulserver.domain.community.dto;

import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.Rank;

public record AuthorSimpleDto(
        Long authorId,
        String authorName, // TODO: 닉네임으로 변경
        Rank authorInstitutionRank,
        String institutionImageUrl
) {
    public static AuthorSimpleDto from(SocialWorker author) {
        return new AuthorSimpleDto(
                author.getId(),
                author.getName(),
                author.getInstitutionRank(),
                author.getNursingInstitution().getProfileImageUrl()
        );
    }
}
