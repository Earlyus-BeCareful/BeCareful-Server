package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import java.time.format.*;

public record SocialWorkerEditResponse(
        String name,
        String nickName,
        Integer birthYymmdd,
        Integer genderCode,
        String phoneNumber,
        String institutionName,
        InstitutionRank institutionRank,
        boolean isAgreedToTerms,
        boolean isAgreedToCollectPersonalInfo,
        boolean isAgreedToReceiveMarketingInfo) {
    public static SocialWorkerEditResponse from(SocialWorker socialWorker) {
        int genderCode = socialWorker.getGender() == Gender.MALE ? 1 : 2;
        if (socialWorker.getBirthday().getYear() >= 2000) {
            genderCode += 2;
        }

        int birthYymmdd = Integer.parseInt(socialWorker.getBirthday().format(DateTimeFormatter.ofPattern("yyMMdd")));

        return new SocialWorkerEditResponse(
                socialWorker.getName(),
                socialWorker.getNickname(),
                birthYymmdd,
                genderCode,
                socialWorker.getPhoneNumber(),
                socialWorker.getNursingInstitution().getName(),
                socialWorker.getInstitutionRank(),
                socialWorker.isAgreedToTerms(),
                socialWorker.isAgreedToCollectPersonalInfo(),
                socialWorker.isAgreedToReceiveMarketingInfo());
    }
}
