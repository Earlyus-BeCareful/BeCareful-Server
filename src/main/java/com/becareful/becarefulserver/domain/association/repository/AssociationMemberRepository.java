package com.becareful.becarefulserver.domain.association.repository;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssociationMemberRepository extends JpaRepository<AssociationMember, Long> {
    Optional<AssociationMember> findByIdAndName(@NotNull Long id, @NotBlank String name);

    Integer countByAssociation(Association association);

    Optional<AssociationMember> findByAssociationAndAssociationRank(Association association, AssociationRank associationRank);

    List<AssociationMember> findAllByAssociation(Association association);

    Integer countByAssociationAndAssociationRank(Association association, AssociationRank associationRank);
}
