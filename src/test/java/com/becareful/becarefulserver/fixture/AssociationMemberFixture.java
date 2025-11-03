package com.becareful.becarefulserver.fixture;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;

public class AssociationMemberFixture {

    public static AssociationMember CHAIRMAN = AssociationMember.createChairman(
            SocialWorkerFixture.SOCIAL_WORKER_1, AssociationFixture.JEONJU_ASSOCIATION, true, true, true);
}
