package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.dto.response.AssociationInfo;
import com.becareful.becarefulserver.domain.community.vo.CommunityAccessStatus;

public record CommunityAccessResponse(CommunityAccessStatus status, AssociationInfo associationInfo) {
    public static CommunityAccessResponse approved(Association association, Integer associationMemberCount) {
        return new CommunityAccessResponse(
                CommunityAccessStatus.APPROVED,
                new AssociationInfo(association.getId(), association.getName(), associationMemberCount));
    }

    public static CommunityAccessResponse rejected() {
        return new CommunityAccessResponse(CommunityAccessStatus.REJECTED, null);
    }

    public static CommunityAccessResponse pending() {
        return new CommunityAccessResponse(CommunityAccessStatus.PENDING, null);
    }

    public static CommunityAccessResponse notApplied() {
        return new CommunityAccessResponse(CommunityAccessStatus.NOT_APPLIED, null);
    }
}
