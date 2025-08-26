package com.becareful.becarefulserver.domain.association.dto.request;

import com.becareful.becarefulserver.domain.socialworker.domain.vo.*;
import jakarta.validation.constraints.*;

public record UpdateAssociationChairmanRequest(
        @NotBlank String newChairmanName,
        @NotBlank String newChairmanNickName,
        @NotNull String newChairmanPhoneNUmber,
        @NotNull AssociationRank nextRankOfCurrentChairman) {}
