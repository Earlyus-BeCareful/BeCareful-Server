package com.becareful.becarefulserver.domain.association.repository;

import com.becareful.becarefulserver.domain.association.domain.AssociationMembershipRequest;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AssociationMembershipRequestRepository extends JpaRepository<AssociationMembershipRequest, Long> {
    Optional<AssociationMembershipRequest> findBySocialWorker(SocialWorker socialWorker);
}
