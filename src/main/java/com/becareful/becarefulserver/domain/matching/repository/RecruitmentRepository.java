package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    @Query("""
    select r
      from Recruitment r
     where r.elderly.nursingInstitution.id = :institutionId
""")
    List<Recruitment> findAllByInstitutionId(Long institutionId);

    boolean existsByElderly(Elderly elderly);
}
