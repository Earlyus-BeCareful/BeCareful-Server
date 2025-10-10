package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.matching.dto.ElderlyDto;
import com.becareful.becarefulserver.domain.matching.dto.response.SocialWorkerRecruitmentResponse;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.util.List;

public record ElderlyDetailResponse(ElderlyDto elderlyInfo, List<SocialWorkerRecruitmentResponse> recruitments) {
    public static ElderlyDetailResponse of(Elderly elderly, List<SocialWorkerRecruitmentResponse> recruitments) {
        return new ElderlyDetailResponse(ElderlyDto.from(elderly), recruitments);
    }
}
