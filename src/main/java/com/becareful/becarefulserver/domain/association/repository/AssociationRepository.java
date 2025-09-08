package com.becareful.becarefulserver.domain.association.repository;

import com.becareful.becarefulserver.domain.association.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface AssociationRepository extends JpaRepository<Association, Long> {
    List<Association> findByNameContains(String associationName);
}
