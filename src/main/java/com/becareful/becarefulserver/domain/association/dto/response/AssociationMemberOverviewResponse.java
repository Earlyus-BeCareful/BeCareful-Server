package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.dto.MemberSimpleDto;
import java.util.List;

public record AssociationMemberOverviewResponse(
        int memberCount, int pendingApplicationCount, List<MemberSimpleDto> members) {}
