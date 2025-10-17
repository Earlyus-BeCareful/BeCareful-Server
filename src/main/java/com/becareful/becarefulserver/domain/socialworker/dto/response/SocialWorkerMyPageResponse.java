package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.dto.AssociationSimpleDto;
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
        @Nullable AssociationSimpleDto associationInfo) {
    public static SocialWorkerMyPageResponse from(SocialWorker socialWorker) {
        NursingInstitution institution = socialWorker.getNursingInstitution();
        Association association = socialWorker.getAssociation();
        return new SocialWorkerMyPageResponse(
                SocialWorkerSimpleDto.from(socialWorker),
                InstitutionDto.from(institution),
                association != null ? AssociationSimpleDto.from(association) : null);
    }
}
