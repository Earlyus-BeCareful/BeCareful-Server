package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.matching.domain.vo.MatchingResultStatus;
import com.becareful.becarefulserver.domain.matching.dto.CaregiverSimpleDto;

public record MatchingCaregiverSimpleResponse(
        MatchedCaregiverResponse caregiverInfo, MatchingResultStatus matchingResultStatus) {
    public static MatchingCaregiverSimpleResponse of(
            WorkApplication workApplication, MatchingResultStatus matchingResultStatus, String careerTitle) {
        return new MatchingCaregiverSimpleResponse(
                MatchedCaregiverResponse.of(workApplication.getCaregiver(), careerTitle), matchingResultStatus);
    }

    // TODO : 필드 풀어서 이 레코드 제거하기, 프론트 응답 형식을 기존과 그대로 맞추기 위해서 이렇게 유지해둠.
    private record MatchedCaregiverResponse(CaregiverSimpleDto caregiverInfo, String applicationTitle) {

        public static MatchedCaregiverResponse of(Caregiver caregiver, String careerTitle) {
            return new MatchedCaregiverResponse(CaregiverSimpleDto.from(caregiver), careerTitle);
        }
    }
}
