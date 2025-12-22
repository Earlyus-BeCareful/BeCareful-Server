package com.becareful.becarefulserver.domain.socialworker.repository;

import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SocialWorkerRepository extends JpaRepository<SocialWorker, Long> {

    Optional<SocialWorker> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByNickname(String nickname);

    List<SocialWorker> findAllByNursingInstitution(NursingInstitution nursingInstitution);

    int countByNursingInstitution(NursingInstitution nursingInstitution);

    @Query("SELECT DISTINCT s.profileImageUrl FROM SocialWorker s WHERE s.profileImageUrl IS NOT NULL")
    List<String> findAllProfileImageUrls();
}
