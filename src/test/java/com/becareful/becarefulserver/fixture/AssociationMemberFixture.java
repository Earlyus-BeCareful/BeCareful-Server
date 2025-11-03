package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;

import java.time.LocalDate;

import static com.becareful.becarefulserver.fixture.NursingInstitutionFixture.NURSING_INSTITUTION;

public class AssociationMemberFixture {

    public static AssociationMember CHAIRMAN = AssociationMember.createChairman(
            SocialWorkerFixture.SOCIAL_WORKER_1,
            AssociationFixture.JEONJU_ASSOCIATION,
            true,
            true,
            true);
}
