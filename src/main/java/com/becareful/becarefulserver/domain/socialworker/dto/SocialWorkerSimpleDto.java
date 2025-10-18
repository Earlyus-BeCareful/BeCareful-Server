package com.becareful.becarefulserver.domain.socialworker.dto;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;

public record SocialWorkerSimpleDto(
        String name,
        String nickName,
        String profileImageUrl,
        String phoneNumber,
        Integer age,
        Gender gender,
        InstitutionRank institutionRank,
        AssociationRank associationRank) {
    public static SocialWorkerSimpleDto from(SocialWorker socialWorker) {
        AssociationMember member = socialWorker.getAssociationMember();
        return new SocialWorkerSimpleDto(
                socialWorker.getName(),
                socialWorker.getNickname(),
                socialWorker.getNursingInstitution().getProfileImageUrl(),
                socialWorker.getPhoneNumber(),
                socialWorker.getAge(),
                socialWorker.getGender(),
                socialWorker.getInstitutionRank(),
                member != null ? member.getAssociationRank() : AssociationRank.NONE);
    }
}
