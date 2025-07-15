package com.becareful.becarefulserver.domain.nursing_institution.repository;

import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NursingInstitutionRepository extends JpaRepository<NursingInstitution, Long> {
    boolean existsById(@NotNull Long institutionId);

    boolean existsByAddress_StreetAddress(String address_StreetAddress);

    List<NursingInstitution> findAllByNameContains(String institutionName);

    Optional<NursingInstitution> findByName(String name);

    boolean existsByCode(@NotNull String nursingInstitutionCode);
}
