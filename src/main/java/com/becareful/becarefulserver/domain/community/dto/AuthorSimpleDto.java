package com.becareful.becarefulserver.domain.community.dto;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;

public record AuthorSimpleDto(
        Long authorId, String authorNickname, InstitutionRank authorInstitutionRank, String institutionImageUrl) {
    public static AuthorSimpleDto from(AssociationMember author) {
        return new AuthorSimpleDto(
                author.getId(),
                author.getNickname(),
                author.getInstitutionRank(),
                author.getNursingInstitution().getProfileImageUrl());
    }
}
