package com.becareful.becarefulserver.domain.association.dto;

import com.becareful.becarefulserver.domain.nursing_institution.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;

public record MemberSimpleDto(
        Long memberId,
        String name,
        String phoneNumber,
        AssociationRank associationRank,
        String institutionName,
        InstitutionRank institutionRank,
        String institutionImageUrl) {
    public static MemberSimpleDto of(SocialWorker member) {
        return new MemberSimpleDto(
                member.getId(),
                member.getName(),
                member.getPhoneNumber(),
                member.getAssociationRank(),
                member.getNursingInstitution().getName(),
                member.getInstitutionRank(),
                member.getNursingInstitution().getProfileImageUrl());
    }
}
