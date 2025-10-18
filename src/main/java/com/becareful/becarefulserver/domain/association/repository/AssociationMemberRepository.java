package com.becareful.becarefulserver.domain.association.repository;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.domain.vo.AssociationRank;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociationMemberRepository extends JpaRepository<AssociationMember, Long> {

    Optional<AssociationMember> findByPhoneNumber(String phoneNumber);

    Optional<AssociationMember> findByAssociationAndAssociationRank(
            Association association, AssociationRank associationRank);

    List<AssociationMember> findAllByAssociation(Association association);

    int countByAssociationAndAssociationRank(Association association, AssociationRank associationRank);

    int countByAssociation(Association association);
}
