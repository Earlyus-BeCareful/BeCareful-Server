package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.association.dto.response.AssociationMyResponse;
import com.becareful.becarefulserver.domain.community.domain.vo.CommunityAccessStatus;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;

public record CommunityAccessResponse(
        CommunityAccessStatus accessStatus, String associationName, AssociationMyResponse associationInfo) {

    public static CommunityAccessResponse alreadyApproved(SocialWorker socialWorker, Integer associationMemberCount) {
        return new CommunityAccessResponse(
                CommunityAccessStatus.ALREADY_APPROVED,
                null,
                AssociationMyResponse.from(socialWorker.getAssociation(), associationMemberCount));
    }

    public static CommunityAccessResponse approved(
            SocialWorker socialWorker, String associationName, Integer associationMemberCount) {
        return new CommunityAccessResponse(
                CommunityAccessStatus.APPROVED,
                associationName,
                AssociationMyResponse.from(socialWorker.getAssociation(), associationMemberCount));
    }

    public static CommunityAccessResponse rejected(SocialWorker socialWorker, String associationName) {
        return new CommunityAccessResponse(CommunityAccessStatus.REJECTED, associationName, null);
    }

    public static CommunityAccessResponse pending(SocialWorker socialWorker, String associationName) {
        return new CommunityAccessResponse(CommunityAccessStatus.PENDING, associationName, null);
    }

    public static CommunityAccessResponse notApplied(SocialWorker socialWorker) {
        return new CommunityAccessResponse(CommunityAccessStatus.NOT_APPLIED, null, null);
    }
}
