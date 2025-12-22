package com.becareful.becarefulserver.domain.socialworker.dto;

import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.nursing_institution.dto.InstitutionSimpleDto;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import java.time.format.DateTimeFormatter;

public record SocialWorkerDto(
        String name,
        String nickName,
        String birthday,
        Integer genderCode,
        String phoneNumber,
        String profileImageUrl,
        InstitutionSimpleDto institutionInfo,
        InstitutionRank institutionRank) {

    public static SocialWorkerDto from(SocialWorker socialWorker) {
        String birthday = socialWorker.getBirthday().format(DateTimeFormatter.ofPattern("yyMMdd"));
        return new SocialWorkerDto(
                socialWorker.getName(),
                socialWorker.getNickname(),
                birthday,
                socialWorker.getGenderCode(),
                socialWorker.getPhoneNumber(),
                socialWorker.getProfileImageUrl(),
                InstitutionSimpleDto.from(socialWorker.getNursingInstitution()),
                socialWorker.getInstitutionRank());
    }
}
