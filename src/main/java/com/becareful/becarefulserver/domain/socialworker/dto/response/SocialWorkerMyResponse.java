package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.dto.InstitutionDto;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.dto.SocialWorkerSimpleDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SocialWorkerMyResponse(
        SocialWorkerSimpleDto socialWorkerInfo, InstitutionDto institutionInfo, @Nullable String associationName) {
    public static SocialWorkerMyResponse from(SocialWorker socialWorker) {
        NursingInstitution institution = socialWorker.getNursingInstitution();
        Association association = socialWorker.getAssociation();
        return new SocialWorkerMyResponse(
                SocialWorkerSimpleDto.from(socialWorker),
                InstitutionDto.from(institution),
                association != null ? association.getName() : null);
    }
}
