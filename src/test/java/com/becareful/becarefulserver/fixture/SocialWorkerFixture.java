package com.becareful.becarefulserver.fixture;

import static com.becareful.becarefulserver.fixture.NursingInstitutionFixture.NURSING_INSTITUTION;

import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import java.time.LocalDate;

public class SocialWorkerFixture {

    public static SocialWorker SOCIAL_WORKER_1 = SocialWorker.create(
            "김복지",
            "nickname",
            LocalDate.of(2000, 5, 9),
            Gender.FEMALE,
            "010-1234-5678",
            InstitutionRank.SOCIAL_WORKER,
            true,
            NURSING_INSTITUTION);

    public static SocialWorker SOCIAL_WORKER_MANAGER = SocialWorker.create(
            "박복지",
            "nickname",
            LocalDate.of(2000, 5, 9),
            Gender.FEMALE,
            "010-1234-5678",
            InstitutionRank.SOCIAL_WORKER,
            true,
            NURSING_INSTITUTION);
}
