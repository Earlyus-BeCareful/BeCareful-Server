package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.dto.SocialWorkerSimpleDto;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record SocialWorkerHomeResponse(
        SocialWorkerSimpleDto socialWorkerInfo,
        boolean hasNewChat,
        InstitutionInfo institutionInfo,
        MatchingStatistics matchingStatistics,
        ApplicationStatistics applicationStatistics,
        List<ElderlySimpleDto> matchingElderlyList) {

    public static SocialWorkerHomeResponse of(
            SocialWorker socialWorker,
            boolean hasNewChat,
            Integer elderlyCount,
            Integer socialWorkerCount,
            List<SocialWorkerSimpleDto> socialWorkerList,
            Integer matchingProcessingCount,
            Integer recentlyMatchedCount,
            Integer totalMatchedCount,
            Integer appliedCaregiverCount,
            Double averageAppliedCaregiver,
            Double averageApplyingRate,
            List<ElderlySimpleDto> matchingElderlyList) {
        return SocialWorkerHomeResponse.builder()
                .socialWorkerInfo(SocialWorkerSimpleDto.from(socialWorker))
                .hasNewChat(hasNewChat)
                .institutionInfo(new InstitutionInfo(
                        socialWorker.getNursingInstitution().getName(),
                        elderlyCount,
                        socialWorkerCount,
                        socialWorkerList))
                .matchingStatistics(
                        new MatchingStatistics(matchingProcessingCount, recentlyMatchedCount, totalMatchedCount))
                .applicationStatistics(new ApplicationStatistics(
                        appliedCaregiverCount, averageAppliedCaregiver.intValue(), averageApplyingRate.intValue()))
                .matchingElderlyList(matchingElderlyList)
                .build();
    }

    private record InstitutionInfo(
            String institutionName,
            Integer elderlyCount,
            Integer socialWorkerCount,
            List<SocialWorkerSimpleDto> socialWorkerList) {}

    private record MatchingStatistics(
            Integer matchingProcessingCount, Integer recentlyMatchedCount, Integer totalMatchingCompletedCount) {}

    private record ApplicationStatistics(
            Integer appliedCaregiverCount, Integer averageAppliedCaregiver, Integer averageApplyingRate) {}
}
