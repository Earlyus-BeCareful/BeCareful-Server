package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.FacilityType;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import java.time.LocalDate;
import java.util.EnumSet;

public record AssociationMemberDetailInfoResponse(
        Long memberId,
        String name,
        String nickName,
        String phoneNumber,
        Integer age,
        Gender gender,
        String institutionImageUrl,
        String institutionName,
        Integer institutionOpenYear,
        LocalDate institutionLastUpdate,
        EnumSet<FacilityType> facilityTypes,
        String institutionPhoneNumber,
        String associationName,
        AssociationRank associationRank,
        InstitutionRank institutionRank) {
    public static AssociationMemberDetailInfoResponse of(
            SocialWorker member, Integer age, NursingInstitution institution, Association association) {
        return new AssociationMemberDetailInfoResponse(
                member.getId(),
                member.getName(),
                member.getNickname(),
                member.getPhoneNumber(),
                age,
                member.getGender(),
                association.getProfileImageUrl(),
                institution.getName(),
                institution.getOpenYear(),
                institution.getUpdateDate().toLocalDate(),
                institution.getFacilityTypes(),
                institution.getInstitutionPhoneNumber(),
                association.getName(),
                member.getAssociationRank(),
                member.getInstitutionRank());
    }
}
