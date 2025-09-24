package com.becareful.becarefulserver.domain.socialworker.repository;

import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ElderlyRepository extends JpaRepository<Elderly, Long> {

    List<Elderly> findAllByNursingInstitution(NursingInstitution nursingInstitution);

    List<Elderly> findByNursingInstitutionAndNameContaining(NursingInstitution nursingInstitution, String name);
}
