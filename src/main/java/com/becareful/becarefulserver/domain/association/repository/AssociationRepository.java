package com.becareful.becarefulserver.domain.association.repository;

import com.becareful.becarefulserver.domain.association.domain.Association;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssociationRepository extends JpaRepository<Association,Long> {

}
