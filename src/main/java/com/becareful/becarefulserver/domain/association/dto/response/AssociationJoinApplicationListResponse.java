package com.becareful.becarefulserver.domain.association.dto.response;

import com.becareful.becarefulserver.domain.association.dto.JoinApplicationSimpleDto;
import java.util.List;

public record AssociationJoinApplicationListResponse(int count, List<JoinApplicationSimpleDto> applications) {}
