package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.association.dto.response.*;

public record CommunityHomeBasicInfoResponse(boolean hasNewChat, AssociationMyResponse associationInfo) {
    public static CommunityHomeBasicInfoResponse of(boolean hasNewChat, AssociationMyResponse associationInfo) {
        return new CommunityHomeBasicInfoResponse(hasNewChat, associationInfo);
    }
}
