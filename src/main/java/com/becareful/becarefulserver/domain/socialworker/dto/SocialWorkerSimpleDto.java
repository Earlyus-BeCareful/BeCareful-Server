package com.becareful.becarefulserver.domain.socialworker.dto;

import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;

public record SocialWorkerSimpleDto(
        String name, String nickName, String phoneNumber, Integer age, Gender gender, InstitutionRank institutionRank) {
    public static SocialWorkerSimpleDto from(SocialWorker socialWorker) {
        return new SocialWorkerSimpleDto(
                socialWorker.getName(),
                socialWorker.getNickname(),
                socialWorker.getPhoneNumber(),
                socialWorker.getAge(),
                socialWorker.getGender(),
                socialWorker.getInstitutionRank());
    }
}
