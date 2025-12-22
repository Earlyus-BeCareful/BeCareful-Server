package com.becareful.becarefulserver.common;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.repository.AssociationMemberRepository;
import com.becareful.becarefulserver.domain.association.repository.AssociationRepository;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.vo.CaregiverInfo;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.nursing_institution.repository.NursingInstitutionRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.fixture.AssociationMemberFixture;
import com.becareful.becarefulserver.fixture.NursingInstitutionFixture;
import com.becareful.becarefulserver.fixture.SocialWorkerFixture;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public class IntegrationTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private SocialWorkerRepository socialworkerRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private NursingInstitutionRepository institutionRepository;

    @Autowired
    private AssociationMemberRepository associationMemberRepository;

    @Autowired
    private CaregiverRepository caregiverRepository;

    @BeforeEach
    public void cleanDatabase() {
        databaseCleaner.clean();
        NursingInstitutionFixture.NURSING_INSTITUTION = NursingInstitutionFixture.create();
        NursingInstitution institution = institutionRepository.save(NursingInstitutionFixture.NURSING_INSTITUTION);
        Association savedAssociation = associationRepository.save(Association.create("전주완주장기요양협회", "url", 2000));
        SocialWorkerFixture.SOCIAL_WORKER_1 = SocialWorker.create(
                "김복지",
                "nickname",
                LocalDate.of(2000, 5, 9),
                Gender.FEMALE,
                "01012345678",
                InstitutionRank.SOCIAL_WORKER,
                "default",
                true,
                institution);
        SocialWorkerFixture.SOCIAL_WORKER_MANAGER = SocialWorker.create(
                "박복지",
                "nickname",
                LocalDate.of(2000, 5, 9),
                Gender.FEMALE,
                "01099990000",
                InstitutionRank.SOCIAL_WORKER,
                "default",
                true,
                institution);

        socialworkerRepository.save(SocialWorkerFixture.SOCIAL_WORKER_1);
        socialworkerRepository.save(SocialWorkerFixture.SOCIAL_WORKER_MANAGER);

        AssociationMemberFixture.CHAIRMAN = AssociationMember.createChairman(
                SocialWorkerFixture.SOCIAL_WORKER_1, savedAssociation, true, true, true);

        associationMemberRepository.save(AssociationMemberFixture.CHAIRMAN);
        SocialWorkerFixture.SOCIAL_WORKER_1.joinAssociation(AssociationMemberFixture.CHAIRMAN);

        caregiverRepository.save(Caregiver.create(
                "caregiver",
                LocalDate.of(1990, 1, 1),
                Gender.FEMALE,
                "01099990000",
                null,
                "서울특별시",
                "상세주소",
                new CaregiverInfo(false, false, null, null, null),
                true));
    }
}
