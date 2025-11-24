package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.FacilityType;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.association.domain.AssociationRank;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.util.EnumSet;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SocialWorkerMyInfo(
        String name,
        String nickName,
        String phoneNumber,
        Integer age,
        Gender gender,
        String institutionName,
        String institutionImageUrl,
        LocalDate institutionLastUpdate,
        int institutionOpenYear,
        EnumSet<FacilityType> facilityTypes,
        String institutionPhoneNumber,
        InstitutionRank institutionRank,
        @Nullable String associationName,
        @Nullable AssociationRank associationRank) {
    public static SocialWorkerMyInfo of(
            SocialWorker member, Integer age, NursingInstitution institution, Association association) {
        return new SocialWorkerMyInfo(
                member.getName(),
                member.getNickname(),
                member.getPhoneNumber(),
                age,
                member.getGender(),
                institution.getName(),
                institution.getProfileImageUrl(),
                institution.getUpdateDate().toLocalDate(),
                institution.getOpenYear(),
                institution.getFacilityTypes(),
                institution.getInstitutionPhoneNumber(),
                member.getInstitutionRank(),
                association != null ? association.getName() : null,
                association != null ? member.getAssociationRank() : null);
    }
}
