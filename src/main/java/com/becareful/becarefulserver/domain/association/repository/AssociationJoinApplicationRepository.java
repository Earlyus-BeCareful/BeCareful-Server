package com.becareful.becarefulserver.domain.association.repository;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationJoinApplication;
import com.becareful.becarefulserver.domain.association.vo.AssociationJoinApplicationStatus;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociationJoinApplicationRepository extends JpaRepository<AssociationJoinApplication, Long> {
    Optional<AssociationJoinApplication> findBySocialWorker(SocialWorker socialWorker);

    List<AssociationJoinApplication> findAllByAssociationAndStatus(
            Association association, AssociationJoinApplicationStatus status);

    Integer countByAssociationAndStatus(
            Association association, AssociationJoinApplicationStatus associationJoinApplicationStatus);
}
