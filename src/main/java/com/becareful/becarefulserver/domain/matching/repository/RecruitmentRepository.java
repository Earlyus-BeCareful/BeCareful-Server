package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    @Query(
            """
        select r
          from Recruitment r
         where r.elderly.nursingInstitution.id = :institutionId
    """)
    List<Recruitment> findAllByInstitutionId(Long institutionId);

    // TODO : QueryDSL 로 이전
    @Query(
            """
        select r
          from Recruitment r
         where r.recruitmentStatus = com.becareful.becarefulserver.domain.matching.domain.RecruitmentStatus.모집중
    """)
    List<Recruitment> findAllByIsRecruiting();

    boolean existsByElderly(Elderly elderly);

    @Query("""
        SELECT r
          FROM Recruitment r
         WHERE r.elderly IN :elderlys
    """)
    List<Recruitment> findAllByElderlys(List<Elderly> elderlys);
}
