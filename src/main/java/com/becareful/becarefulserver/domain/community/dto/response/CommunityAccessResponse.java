package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.dto.response.AssociationMyResponse;
import com.becareful.becarefulserver.domain.community.domain.vo.CommunityAccessStatus;

public record CommunityAccessResponse(
        CommunityAccessStatus accessStatus, String associationName, AssociationMyResponse associationInfo) {

    public static CommunityAccessResponse alreadyApproved(
            AssociationMember associationMember, Integer associationMemberCount) {
        return new CommunityAccessResponse(
                CommunityAccessStatus.ALREADY_APPROVED,
                null,
                AssociationMyResponse.from(associationMember.getAssociation(), associationMemberCount));
    }

    public static CommunityAccessResponse approved(
            AssociationMember associationMember, String associationName, Integer associationMemberCount) {
        return new CommunityAccessResponse(
                CommunityAccessStatus.APPROVED,
                associationName,
                AssociationMyResponse.from(associationMember.getAssociation(), associationMemberCount));
    }

    public static CommunityAccessResponse rejected(String associationName) {
        return new CommunityAccessResponse(CommunityAccessStatus.REJECTED, associationName, null);
    }

    public static CommunityAccessResponse pending(String associationName) {
        return new CommunityAccessResponse(CommunityAccessStatus.PENDING, associationName, null);
    }

    public static CommunityAccessResponse notApplied() {
        return new CommunityAccessResponse(CommunityAccessStatus.NOT_APPLIED, null, null);
    }
}
