package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.common.vo.Gender;
import com.becareful.becarefulserver.domain.nursingInstitution.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import java.time.LocalDate;

public class SocialWorkerFixture {

    public static SocialWorker SOCIAL_WORKER_1 = SocialWorker.create(
            "김복지",
            "nickname",
            LocalDate.of(2000, 5, 9),
            Gender.FEMALE,
            "010-1234-5678",
            InstitutionRank.SOCIALWORKER,
            AssociationRank.NONE,
            true,
            NursingInstitutionFixture.NURSING_INSTITUTION,
            Association.create("test"));

    public static SocialWorker SOCIAL_WORKER_MANAGER = SocialWorker.create(
            "박복지",
            "nickname",
            LocalDate.of(2000, 5, 9),
            Gender.FEMALE,
            "010-1234-5678",
            InstitutionRank.SOCIALWORKER,
            AssociationRank.MEMBER,
            true,
            NursingInstitutionFixture.NURSING_INSTITUTION,
            Association.create("test"));
}
