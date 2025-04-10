package com.becareful.becarefulserver.domain.nursingInstitution.repository;

import com.becareful.becarefulserver.domain.nursingInstitution.domain.NursingInstitution;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NursingInstitutionRepository extends JpaRepository<NursingInstitution, Long> {
    boolean existsById(@NotNull Long institutionId);
    boolean existsByAddress_StreetAddress(String address_StreetAddress);
    List<NursingInstitution> findAllByNameContains(String institutionName);
    Optional<NursingInstitution> findByName(String name);
    boolean existsByCode(@NotNull String nursingInstitutionCode);
}
