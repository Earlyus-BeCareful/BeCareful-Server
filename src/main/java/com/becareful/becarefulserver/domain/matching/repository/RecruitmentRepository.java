package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import com.becareful.becarefulserver.domain.matching.domain.RecruitmentStatus;
import com.becareful.becarefulserver.domain.matching.dto.response.SocialWorkerRecruitmentResponse;
import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {

    @Query(
            """
        SELECT r
          FROM Recruitment r
         WHERE r.elderly.nursingInstitution = :institution
           AND r.recruitmentStatus IN :recruitmentStatus
    """)
    Page<Recruitment> findAllByInstitutionAndRecruitmentStatusIn(
            NursingInstitution institution, List<RecruitmentStatus> recruitmentStatus, Pageable pageable);

    @Query(
            """
        SELECT r
          FROM Recruitment r
         WHERE r.elderly.nursingInstitution = :institution
           AND r.recruitmentStatus IN :recruitmentStatus
           AND (r.title LIKE %:keyword% OR r.elderly.name LIKE %:keyword%)
    """)
    Page<Recruitment> searchByInstitutionAndElderlyNameOrRecruitmentTitle(
            NursingInstitution institution,
            List<RecruitmentStatus> recruitmentStatus,
            String keyword,
            Pageable pageable);

    List<Recruitment> findAllByElderly(Elderly elderly);

    // TODO : QueryDSL 로 이전
    @Query(
            """
        select count(r)
          from Recruitment r
         where r.recruitmentStatus = com.becareful.becarefulserver.domain.matching.domain.RecruitmentStatus.모집중
    """)
    Long countByIsRecruiting();

    List<Recruitment> findAllByElderlyIn(List<Elderly> elderlys);
}
