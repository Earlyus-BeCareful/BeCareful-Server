package com.becareful.becarefulserver.domain.community.dto.response;

import com.becareful.becarefulserver.domain.association.dto.response.*;

public record CommunityHomeBasicInfoResponse(AssociationMyResponse associationInfo) {
    public static CommunityHomeBasicInfoResponse of(AssociationMyResponse associationInfo) {
        return new CommunityHomeBasicInfoResponse(associationInfo);
    }
}
