package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.dto.MemberSimpleDto;
import java.util.List;

public record AssociationMemberListResponse(int count, List<MemberSimpleDto> members) {}
