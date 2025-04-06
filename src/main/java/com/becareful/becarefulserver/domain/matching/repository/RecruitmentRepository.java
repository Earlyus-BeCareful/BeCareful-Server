package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    List<Recruitment> findByElderly_NursingInstitution_Id(String institutionId);

    boolean existsByElderly(Elderly elderly);
}
