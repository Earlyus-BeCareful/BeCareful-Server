package com.becareful.becarefulserver.domain.auth.dto.response;

import com.becareful.becarefulserver.domain.association.domain.vo.AssociationRank;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;

public record RegisteredUserLoginResponse(
        String realName, String nickName, AssociationRank associationRank, InstitutionRank institutionRank) {}
