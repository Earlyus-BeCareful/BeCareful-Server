package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.nursing_institution.dto.InstitutionSimpleDto;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.dto.SocialWorkerSimpleDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Builder;

@Builder(access = AccessLevel.PRIVATE)
public record SocialWorkerHomeResponse(
        SocialWorkerSimpleDto socialWorkerInfo,
        InstitutionInfo institutionInfo,
        RecruitmentStatistics recruitmentStatistics,
        List<MatchingProcessingElderlyInfo> matchingProcessingElderlys,
        boolean hasNewChat) {

    public static SocialWorkerHomeResponse of(
            SocialWorker loggedInSocialWorker,
            List<SocialWorkerSimpleDto> socialWorkerList,
            List<Recruitment> recruitments,
            Integer elderlyCount,
            boolean hasNewChat) {

        List<MatchingProcessingElderlyInfo> matchingProcessingElderlyInfos = recruitments.stream()
                .filter(r -> r.getRecruitmentStatus().isRecruiting())
                .collect(Collectors.groupingBy(Recruitment::getElderly, Collectors.counting()))
                .entrySet()
                .stream()
                .map(MatchingProcessingElderlyInfo::from)
                .toList();

        return SocialWorkerHomeResponse.builder()
                .socialWorkerInfo(SocialWorkerSimpleDto.from(loggedInSocialWorker))
                .institutionInfo(new InstitutionInfo(
                        InstitutionSimpleDto.from(loggedInSocialWorker.getNursingInstitution()),
                        elderlyCount,
                        socialWorkerList.size(),
                        socialWorkerList))
                .recruitmentStatistics(RecruitmentStatistics.from(recruitments))
                .matchingProcessingElderlys(matchingProcessingElderlyInfos)
                .hasNewChat(hasNewChat)
                .build();
    }

    private record InstitutionInfo(
            InstitutionSimpleDto institutionDetail,
            Integer elderlyCount,
            Integer socialWorkerCount,
            List<SocialWorkerSimpleDto> socialWorkerList) {}

    private record RecruitmentStatistics(
            Integer recruitmentProcessingCount,
            Integer recentlyCompletedCount,
            Integer totalRecruitmentCompletedCount) {

        public static RecruitmentStatistics from(List<Recruitment> recruitments) {
            int processingRecruitmentCount = 0; // 진행중인 공고
            int recentlyCompletedRecruitmentCount = 0; // 최근 일주일 동안 완료된 공고
            int totalCompletedRecruitmentCount = 0; // 누적 완료된 공고

            for (Recruitment recruitment : recruitments) {
                if (recruitment.getRecruitmentStatus().isRecruiting()) {
                    processingRecruitmentCount++;
                } else {
                    if (recruitment.getUpdateDate().isAfter(LocalDateTime.now().minusDays(7))) {
                        recentlyCompletedRecruitmentCount++;
                    }
                    totalCompletedRecruitmentCount++;
                }
            }

            return new RecruitmentStatistics(
                    processingRecruitmentCount, recentlyCompletedRecruitmentCount, totalCompletedRecruitmentCount);
        }
    }

    private record MatchingProcessingElderlyInfo(ElderlySimpleDto elderlyDetail, Long recruitmentCount) {
        public static MatchingProcessingElderlyInfo from(Map.Entry<Elderly, Long> elderlyInfo) {
            return new MatchingProcessingElderlyInfo(
                    ElderlySimpleDto.from(elderlyInfo.getKey()), elderlyInfo.getValue());
        }
    }
}
