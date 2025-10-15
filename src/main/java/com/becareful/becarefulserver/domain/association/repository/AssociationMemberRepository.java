package com.becareful.becarefulserver.domain.association.repository;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociationMemberRepository extends JpaRepository<AssociationMember, Long> {

    Optional<AssociationMember> findByPhoneNumber(String phoneNumber);
}
