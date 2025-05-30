package com.becareful.becarefulserver.domain.nursingInstitution.dto.response;

import java.util.List;

public record NursingInstitutionSearchResponse(List<NursingInstitutionSimpleInfo> nursingInstitutionSimpleInfoList) {
    public record NursingInstitutionSimpleInfo(
            Long institutionId,
            String institutionName,
            String institutionStreetAddress,
            String institutionDetailAddress) {}
}
