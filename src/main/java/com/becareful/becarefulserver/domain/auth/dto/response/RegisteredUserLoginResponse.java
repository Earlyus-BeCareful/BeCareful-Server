package com.becareful.becarefulserver.domain.auth.dto.response;

import com.becareful.becarefulserver.domain.nursing_institution.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;

public record RegisteredUserLoginResponse(
        String realName, String nickName, AssociationRank associationRank, InstitutionRank institutionRank) {}
