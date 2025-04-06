package com.becareful.becarefulserver.domain.socialworker.repository;

import com.becareful.becarefulserver.domain.socialworker.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialworkerRepository extends JpaRepository<SocialWorker, Long> {

    Optional<SocialWorker> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    Integer countByNursingInstitution(NursingInstitution nursingInstitution);
}
