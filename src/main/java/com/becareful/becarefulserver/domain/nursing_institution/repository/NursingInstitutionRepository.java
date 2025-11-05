package com.becareful.becarefulserver.domain.nursing_institution.repository;

import com.becareful.becarefulserver.domain.nursing_institution.domain.*;
import java.util.*;
import org.jetbrains.annotations.*;
import org.springframework.data.jpa.repository.*;

public interface NursingInstitutionRepository extends JpaRepository<NursingInstitution, Long> {
    boolean existsById(@NotNull Long institutionId);

    boolean existsByAddress_StreetAddress(String address_StreetAddress);

    List<NursingInstitution> findAllByNameContains(String institutionName);

    Optional<NursingInstitution> findByName(String name);

    boolean existsByCode(@NotNull String nursingInstitutionCode);

    @Query("SELECT n.profileImageUrl FROM NursingInstitution n WHERE n.profileImageUrl IS NOT NULL")
    Set<String> findAllProfileImageUrls();
}
