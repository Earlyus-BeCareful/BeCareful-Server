package com.becareful.becarefulserver.domain.socialworker.repository;

import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;

public interface ElderlyRepository extends JpaRepository<Elderly, Long> {

    List<Elderly> findAllByNursingInstitution(NursingInstitution nursingInstitution);

    List<Elderly> findByNursingInstitutionAndNameContaining(NursingInstitution nursingInstitution, String name);

    @Query("SELECT e.profileImageUrl FROM Elderly e WHERE e.profileImageUrl IS NOT NULL")
    Set<String> findAllProfileImageUrls();
}
