package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.domain.Association;
import java.util.List;

public record AssociationSearchResponse(Integer count, List<AssociationSimpleInfo> associationSimpleInfoList) {

    public record AssociationSimpleInfo(Long associationId, String associationName, Integer associationMemberCount) {
        public static AssociationSimpleInfo of(Association association, Integer memberCount) {
            return new AssociationSimpleInfo(association.getId(), association.getName(), memberCount);
        }
    }
}
