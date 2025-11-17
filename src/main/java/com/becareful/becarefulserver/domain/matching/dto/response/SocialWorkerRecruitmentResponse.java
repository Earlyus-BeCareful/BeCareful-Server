package com.becareful.becarefulserver.domain.matching.dto.response;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentSimpleDto;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import lombok.Getter;

public record SocialWorkerRecruitmentResponse(
        RecruitmentSimpleDto recruitmentInfo,
        ElderlySimpleDto elderlyInfo,
        /*
         요양보호사는 CaregiverRecruitmentStatus 를 사용하기 때문에,
         RecruitmentStatus 를 RecruitmentSimpleDto 에 넣지 않고 비슷한 구조로 밖으로 뺐습니다.
        */
        String recruitmentStatus,
        long matchingCount,
        long applyCount) {

    // RecruitmentRepository 에서 프로젝션에 사용
    public SocialWorkerRecruitmentResponse(
            Recruitment recruitment, Elderly elderly, long matchingCount, long applyCount) {
        this(
                RecruitmentSimpleDto.from(recruitment),
                ElderlySimpleDto.from(elderly),
                switch (recruitment.getRecruitmentStatus()) {
                    case 모집중 -> SocialWorkerRecruitmentStatus.매칭중.value;
                    case 조율중 -> SocialWorkerRecruitmentStatus.조율중.value;
                    case 모집완료 -> SocialWorkerRecruitmentStatus.매칭완료.value;
                    case 공고마감 -> SocialWorkerRecruitmentStatus.공고마감.value;
                },
                matchingCount,
                applyCount);
    }

    @Getter
    private enum SocialWorkerRecruitmentStatus {
        매칭중("매칭중"),
        조율중("조율중"),
        매칭완료("매칭 완료"),
        공고마감("공고 마감");

        SocialWorkerRecruitmentStatus(String value) {
            this.value = value;
        }

        private String value;
    }
}
