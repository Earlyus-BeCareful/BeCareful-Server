package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.nursing_institution.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import java.util.List;
import lombok.Builder;

@Builder
public record SocialWorkerHomeResponse(
        String socialWorkerName,
        InstitutionRank socialWorkerRank,
        String institutionName,
        Integer elderlyCount,
        Integer socialWorkerCount,
        Integer matchingProcessingCount,
        Integer recentlyMatchedCount,
        Integer matchingCompletedCount,
        Integer appliedCaregiverCount,
        Integer averageAppliedCaregiver,
        Integer averageApplyingRate,
        List<SimpleElderlyResponse> matchingElderlyList) {

    public static SocialWorkerHomeResponse of(
            SocialWorker socialWorker,
            Integer elderlyCount,
            Integer socialWorkerCount,
            Long matchingProcessingCount,
            Long recentlyMatchedCount,
            Integer totalMatchedCount,
            Integer appliedCaregiverCount,
            Double averageAppliedCaregiver,
            Double averageApplyingRate,
            List<SimpleElderlyResponse> matchingElderlyList) {
        return SocialWorkerHomeResponse.builder()
                .socialWorkerName(socialWorker.getName())
                .socialWorkerRank(socialWorker.getInstitutionRank())
                .institutionName(socialWorker.getNursingInstitution().getName())
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
