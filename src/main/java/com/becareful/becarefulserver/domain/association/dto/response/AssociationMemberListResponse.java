package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.dto.AssociationMemberSimpleDto;
import java.util.List;

public record AssociationMemberListResponse(int count, List<AssociationMemberSimpleDto> members) {}
