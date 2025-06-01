package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.dto.response.AssociationMyResponse;
import com.becareful.becarefulserver.domain.community.vo.CommunityAccessStatus;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;

public record CommunityAccessResponse(
        CommunityAccessStatus accessStatus, AssociationMyResponse associationInfo, String socialWorkerNickname) {

    public static CommunityAccessResponse alreadyApproved(
            Integer associationMemberCount, SocialWorker socialWorker) {
        return new CommunityAccessResponse(
                CommunityAccessStatus.ALREADY_APPROVED,
                AssociationMyResponse.from(socialWorker.getAssociation(), associationMemberCount),
                socialWorker.getNickname());
    }

    public static CommunityAccessResponse approved(
            Integer associationMemberCount, SocialWorker socialWorker) {
        return new CommunityAccessResponse(
                CommunityAccessStatus.APPROVED,
                AssociationMyResponse.from(socialWorker.getAssociation(), associationMemberCount),
                socialWorker.getNickname());
    }

    public static CommunityAccessResponse rejected(SocialWorker socialWorker) {
        return new CommunityAccessResponse(CommunityAccessStatus.REJECTED, null, socialWorker.getNickname());
    }

    public static CommunityAccessResponse pending(SocialWorker socialWorker) {
        return new CommunityAccessResponse(CommunityAccessStatus.PENDING, null, socialWorker.getNickname());
    }

    public static CommunityAccessResponse notApplied(SocialWorker socialWorker) {
        return new CommunityAccessResponse(CommunityAccessStatus.NOT_APPLIED, null, socialWorker.getNickname());
    }
}
