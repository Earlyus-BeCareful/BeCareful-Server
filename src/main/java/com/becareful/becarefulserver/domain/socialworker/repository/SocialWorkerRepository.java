package com.becareful.becarefulserver.domain.socialworker.repository;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialWorkerRepository extends JpaRepository<SocialWorker, Long> {

    Optional<SocialWorker> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByNickname(String nickname);

    List<SocialWorker> findAllByNursingInstitution(NursingInstitution nursingInstitution);

    Integer countByAssociation(Association association);

    List<SocialWorker> findAllByAssociation(Association association);

    Optional<SocialWorker> findByAssociationAndAssociationRank(
            Association association, AssociationRank associationRank);
}
