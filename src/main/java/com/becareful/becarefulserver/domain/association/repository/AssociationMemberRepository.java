package com.becareful.becarefulserver.domain.association.repository;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssociationMemberRepository extends JpaRepository<AssociationMember, Long> {
    Optional<AssociationMember> findByIdAndName(@NotNull Long id, @NotBlank String name);
}
