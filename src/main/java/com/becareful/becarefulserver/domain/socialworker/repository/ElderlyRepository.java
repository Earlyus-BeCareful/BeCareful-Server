package com.becareful.becarefulserver.domain.socialworker.repository;

import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.nursingInstitution.domain.NursingInstitution;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ElderlyRepository extends JpaRepository<Elderly, Long> {

        List<Elderly> findByNursingInstitution(NursingInstitution nursingInstitution);

        List<Elderly> findByNursingInstitutionAndNameContaining(NursingInstitution nursingInstitution, String name);

}




