package com.becareful.becarefulserver.domain.association.dto.response;

import java.util.List;

public record AssociationMemberListResponse(int count, List<AssociationMemberResponse> members) {
    public static AssociationMemberListResponse from(List<AssociationMemberResponse> members) {
        return new AssociationMemberListResponse(members.size(), members);
    }
}
