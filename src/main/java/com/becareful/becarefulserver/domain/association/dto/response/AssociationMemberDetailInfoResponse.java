package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.FacilityType;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
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
    public static AssociationMemberDetailInfoResponse from(AssociationMember member) {
        return new AssociationMemberDetailInfoResponse(
                member.getId(),
                member.getName(),
                member.getNickname(),
                member.getPhoneNumber(),
                member.getAge(),
                member.getGender(),
                member.getAssociation().getProfileImageUrl(),
                member.getInstitution().getName(),
                member.getInstitution().getOpenYear(),
                member.getInstitution().getUpdateDate().toLocalDate(),
                member.getInstitution().getFacilityTypes(),
                member.getInstitution().getInstitutionPhoneNumber(),
                member.getAssociation().getName(),
                member.getAssociationRank(),
                member.getInstitutionRank());
    }
}
