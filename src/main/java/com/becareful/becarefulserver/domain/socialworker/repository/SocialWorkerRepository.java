package com.becareful.becarefulserver.domain.socialworker.repository;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.nursingInstitution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialWorkerRepository extends JpaRepository<SocialWorker, Long> {

    Optional<SocialWorker> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByNickname(String nickname);
    Integer countByNursingInstitution(NursingInstitution nursingInstitution);
    Integer countByAssociation(Association association);
}
