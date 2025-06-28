package com.becareful.becarefulserver.domain.nursing_institution.dto.response;

import java.util.List;

public record NursingInstitutionSearchResponse(List<NursingInstitutionSimpleInfo> nursingInstitutionSimpleInfoList) {
    public record NursingInstitutionSimpleInfo(
            Long institutionId,
            String institutionName,
            String institutionStreetAddress,
            String institutionDetailAddress) {}
}
