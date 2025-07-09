package com.becareful.becarefulserver.common;

import com.becareful.becarefulserver.domain.nursing_institution.repository.NursingInstitutionRepository;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.fixture.NursingInstitutionFixture;
import com.becareful.becarefulserver.fixture.SocialWorkerFixture;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
    private NursingInstitutionRepository institutionRepository;

    @Transactional
    public void clean() {
        em.createNativeQuery("set foreign_key_checks = 0").executeUpdate();
        em.getMetamodel().getEntities().forEach(entity -> {
            em.createNativeQuery("truncate table " + entity.getName()).executeUpdate();
        });
        em.createNativeQuery("set foreign_key_checks = 1").executeUpdate();
        institutionRepository.save(NursingInstitutionFixture.NURSING_INSTITUTION);
        socialworkerRepository.save(SocialWorkerFixture.SOCIAL_WORKER_1);
        socialworkerRepository.save(SocialWorkerFixture.SOCIAL_WORKER_MANAGER);
    }
}
