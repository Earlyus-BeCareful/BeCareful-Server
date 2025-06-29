package com.becareful.becarefulserver.common;

import com.becareful.becarefulserver.domain.nursing_institution.repository.NursingInstitutionRepository;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.fixture.NursingInstitutionFixture;
import com.becareful.becarefulserver.fixture.SocialWorkerFixture;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DatabaseCleaner {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private SocialWorkerRepository socialworkerRepository;

    @Autowired
    private NursingInstitutionRepository institutionRepository;

    public void clean() {
        institutionRepository.save(NursingInstitutionFixture.NURSING_INSTITUTION);
        socialworkerRepository.save(SocialWorkerFixture.SOCIAL_WORKER_1);
        socialworkerRepository.save(SocialWorkerFixture.SOCIAL_WORKER_MANAGER);
    }
}
