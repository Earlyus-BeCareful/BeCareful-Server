package com.becareful.becarefulserver.domain.socialworker.repository;

import com.becareful.becarefulserver.domain.socialworker.domain.NursingInstitution;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NursingInstitutionRepository extends JpaRepository<NursingInstitution, String> {
    boolean existsById(String institutionId);
}
