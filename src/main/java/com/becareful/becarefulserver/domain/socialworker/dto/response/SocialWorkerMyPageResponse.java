package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.dto.AssociationMemberDto;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.dto.InstitutionDto;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.dto.SocialWorkerSimpleDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SocialWorkerMyPageResponse(
        SocialWorkerSimpleDto socialWorkerInfo,
        InstitutionDto institutionInfo,
        @Nullable AssociationMemberDto associationInfo) {
    public static SocialWorkerMyPageResponse from(SocialWorker socialWorker) {
        NursingInstitution institution = socialWorker.getNursingInstitution();
        AssociationMember associationMember = socialWorker.getAssociationMember();
        return new SocialWorkerMyPageResponse(
                SocialWorkerSimpleDto.from(socialWorker),
                InstitutionDto.from(institution),
                associationMember != null ? AssociationMemberDto.from(associationMember) : null);
    }
}
