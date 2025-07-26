package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;

public record AssociationInfoResponse(
        String associationName,
        Integer associationEstablishedYear,
        Integer associationMemberCount,
        String associationProfileImageUrl,
        String chairmanRealName,
        String chairmanNickName,
        String chairmanPhoneNumber) {
    public static AssociationInfoResponse of(Association association, Integer memberCount, SocialWorker chairman) {
        return new AssociationInfoResponse(
                association.getName(),
                association.getEstablishedYear(),
                memberCount,
                association.getProfileImageUrl(),
                chairman.getName(),
                chairman.getNickname(),
                chairman.getPhoneNumber());
    }
}
