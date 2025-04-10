package com.becareful.becarefulserver.domain.socialworker.repository;

import com.becareful.becarefulserver.domain.nursingInstitution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.Socialworker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialworkerRepository extends JpaRepository<Socialworker, Long> {

    Optional<Socialworker> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    Integer countByNursingInstitution(NursingInstitution nursingInstitution);

    boolean existsByNickName(String nickName);
}
