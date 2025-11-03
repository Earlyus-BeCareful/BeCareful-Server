package com.becareful.becarefulserver.common;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.repository.AssociationMemberRepository;
import com.becareful.becarefulserver.domain.association.repository.AssociationRepository;
import com.becareful.becarefulserver.domain.common.domain.Gender;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.nursing_institution.domain.vo.InstitutionRank;
import com.becareful.becarefulserver.domain.nursing_institution.repository.NursingInstitutionRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.fixture.AssociationMemberFixture;
import com.becareful.becarefulserver.fixture.NursingInstitutionFixture;
import com.becareful.becarefulserver.fixture.SocialWorkerFixture;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SocialWorkerRepository socialworkerRepository;

    @Autowired
    private AssociationRepository associationRepository;

    @Autowired
    private NursingInstitutionRepository institutionRepository;

    @Autowired
    private AssociationMemberRepository associationMemberRepository;

    @Transactional
    public void clean() {
        em.createNativeQuery("set foreign_key_checks = 0").executeUpdate();
        em.getMetamodel().getEntities().forEach(entity -> {
            String tableName = camelToSnake(entity.getName());
            em.createNativeQuery("truncate table " + tableName).executeUpdate();
        });
        em.createNativeQuery("set foreign_key_checks = 1").executeUpdate();
        NursingInstitutionFixture.NURSING_INSTITUTION = NursingInstitutionFixture.create();
        NursingInstitution institution = institutionRepository.save(NursingInstitutionFixture.NURSING_INSTITUTION);
        Association savedAssociation = associationRepository.save(Association.create("전주완주장기요양협회", "url", 2000));
        SocialWorkerFixture.SOCIAL_WORKER_1 = SocialWorker.create(
                "김복지",
                "nickname",
                LocalDate.of(2000, 5, 9),
                Gender.FEMALE,
                "010-1234-5678",
                InstitutionRank.SOCIAL_WORKER,
                true,
                institution);
        SocialWorkerFixture.SOCIAL_WORKER_MANAGER = SocialWorker.create(
                "박복지",
                "nickname",
                LocalDate.of(2000, 5, 9),
                Gender.FEMALE,
                "01099990000",
                InstitutionRank.SOCIAL_WORKER,
                true,
                institution);

        socialworkerRepository.save(SocialWorkerFixture.SOCIAL_WORKER_1);
        socialworkerRepository.save(SocialWorkerFixture.SOCIAL_WORKER_MANAGER);

        AssociationMemberFixture.CHAIRMAN = AssociationMember.createChairman(
                SocialWorkerFixture.SOCIAL_WORKER_1, savedAssociation, true, true, true);

        associationMemberRepository.save(AssociationMemberFixture.CHAIRMAN);
        SocialWorkerFixture.SOCIAL_WORKER_1.joinAssociation(AssociationMemberFixture.CHAIRMAN);
    }

    private String camelToSnake(String value) {
        return value.replaceAll("(?<=[a-z])[A-Z]", "_$0").toLowerCase();
    }
}
