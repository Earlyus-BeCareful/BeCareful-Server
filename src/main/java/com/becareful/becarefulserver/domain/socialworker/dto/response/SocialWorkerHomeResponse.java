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
        RecruitmentStatistics recruitmentStatistics,
        ApplicationStatistics applicationStatistics,
        List<ElderlySimpleDto> matchingElderlyList) {

    public static SocialWorkerHomeResponse of(
            SocialWorker socialWorker,
            Integer elderlyCount,
            Integer caregiverCount,
            List<SocialWorkerSimpleDto> socialWorkerList,
            Integer recruitmentProcessingCount,
            Integer recentlyCompletedCount,
            Integer totalRecruitmentCompletedCount,
            Integer appliedCaregiverCount,
            Double averageAppliedCaregiver,
            Double averageApplyingRate,
            List<ElderlySimpleDto> matchingElderlyList,
            boolean hasNewChat) {
        return SocialWorkerHomeResponse.builder()
                .socialWorkerInfo(SocialWorkerSimpleDto.from(socialWorker))
                .institutionInfo(new InstitutionInfo(
                        socialWorker.getNursingInstitution().getName(),
                        elderlyCount,
                        caregiverCount,
                        socialWorkerList.size(),
                        socialWorkerList))
                .recruitmentStatistics(new RecruitmentStatistics(
                        recruitmentProcessingCount, recentlyCompletedCount, totalRecruitmentCompletedCount))
                .applicationStatistics(new ApplicationStatistics(
                        appliedCaregiverCount, averageAppliedCaregiver.intValue(), averageApplyingRate.intValue()))
                .matchingElderlyList(matchingElderlyList)
                .hasNewChat(hasNewChat)
                .build();
    }

    private record InstitutionInfo(
            String institutionName,
            Integer elderlyCount,
            Integer caregiverCount,
            Integer socialWorkerCount,
            List<SocialWorkerSimpleDto> socialWorkerList) {}

    private record RecruitmentStatistics(
            Integer recruitmentProcessingCount,
            Integer recentlyCompletedCount,
            Integer totalRecruitmentCompletedCount) {}

    private record ApplicationStatistics(
            Integer appliedCaregiverCount, Integer averageAppliedCaregiver, Integer averageApplyingRate) {}
}
