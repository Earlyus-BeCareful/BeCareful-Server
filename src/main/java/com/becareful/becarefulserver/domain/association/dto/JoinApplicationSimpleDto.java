package com.becareful.becarefulserver.domain.association.dto;

import com.becareful.becarefulserver.domain.association.domain.AssociationJoinApplication;
import com.becareful.becarefulserver.domain.nursingInstitution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursingInstitution.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;

public record JoinApplicationSimpleDto(
        Long joinApplicationId,
        String name,
        AssociationRank associationRank,
        String institutionName,
        InstitutionRank institutionRank,
        String institutionImageUrl) {
    public static JoinApplicationSimpleDto of(AssociationJoinApplication application) {
        SocialWorker socialWorker = application.getSocialWorker();
        NursingInstitution institution = socialWorker.getNursingInstitution();
        return new JoinApplicationSimpleDto(
                application.getId(),
                socialWorker.getName(),
                socialWorker.getAssociationRank(),
                institution.getName(),
                socialWorker.getInstitutionRank(),
                institution.getProfileImageUrl());
    }
}
