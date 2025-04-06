package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.Rank;

public class SocialWorkerFixture {

    public static SocialWorker SOCIAL_WORKER_1 = SocialWorker.create(
            "김복지",
            Gender.FEMALE,
            "01012345678",
            "password",
            NursingInstitutionFixture.NURSING_INSTITUTION,
            Rank.SOCIALWORKER,
            true
    );

    public static SocialWorker SOCIAL_WORKER_MANAGER = SocialWorker.create(
            "박복지",
            Gender.FEMALE,
            "01012345679",
            "password",
            NursingInstitutionFixture.NURSING_INSTITUTION,
            Rank.MANAGER,
            true
    );
}
