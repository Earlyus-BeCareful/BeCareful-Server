package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.socialworker.domain.Socialworker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.Rank;

import java.util.List;
import lombok.Builder;

@Builder
public record SocialWorkerHomeResponse(
        String socialWorkerName,
        Rank socialWorkerRank,
        String institutionName,
        Integer elderlyCount,
        Integer socialWorkerCount,
        Integer matchingProcessingCount,
        Integer recentlyMatchedCount,
        Integer matchingCompletedCount,
        Integer appliedCaregiverCount,
        Integer averageAppliedCaregiver,
        Integer averageApplyingRate,
        List<SimpleElderlyResponse> matchingElderlyList
) {

    public static SocialWorkerHomeResponse of(Socialworker socialworker, Integer elderlyCount, Integer socialWorkerCount,
            Long matchingProcessingCount, Long recentlyMatchedCount, Integer totalMatchedCount,
            Integer appliedCaregiverCount, Double averageAppliedCaregiver, Double averageApplyingRate,
            List<SimpleElderlyResponse> matchingElderlyList
            ) {
        return SocialWorkerHomeResponse.builder()
                .socialWorkerName(socialworker.getName())
                .socialWorkerRank(socialworker.getInstitutionRank())
                .institutionName(socialworker.getNursingInstitution().getName())
                .elderlyCount(elderlyCount)
                .socialWorkerCount(socialWorkerCount)
                .matchingProcessingCount(matchingProcessingCount.intValue())
                .recentlyMatchedCount(recentlyMatchedCount.intValue())
                .matchingCompletedCount(totalMatchedCount)
                .appliedCaregiverCount(appliedCaregiverCount)
                .averageAppliedCaregiver(averageAppliedCaregiver.intValue())
                .averageApplyingRate(averageApplyingRate.intValue())
                .matchingElderlyList(matchingElderlyList)
                .build();
    }

}
