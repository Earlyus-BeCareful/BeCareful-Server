package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.Socialworker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.Rank;

public class SocialWorkerFixture {

    public static Socialworker SOCIAL_WORKER_1 = Socialworker.create(
            "김복지",
            Gender.FEMALE,
            "01012345678",
            "password",
            NursingInstitutionFixture.NURSING_INSTITUTION,
            Rank.SOCIALWORKER,
            true
    );

    public static Socialworker SOCIAL_WORKER_MANAGER = Socialworker.create(
            "박복지",
            Gender.FEMALE,
            "01012345679",
            "password",
            NursingInstitutionFixture.NURSING_INSTITUTION,
            Rank.MANAGER,
            true
    );
}
