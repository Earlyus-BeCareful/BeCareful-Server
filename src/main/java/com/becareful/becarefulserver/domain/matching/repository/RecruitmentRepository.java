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
        SELECT new com.becareful.becarefulserver.domain.matching.dto.response.SocialWorkerRecruitmentResponse(
                   r,
                   e,
                   COUNT(a),
                   COUNT(a)
               )
          FROM Recruitment r
          JOIN r.elderly e
          LEFT JOIN Application a ON a.recruitment = r
         WHERE r.elderly.nursingInstitution = :institution
           AND r.recruitmentStatus IN :recruitmentStatus
         GROUP BY r, e
    """)
    Page<SocialWorkerRecruitmentResponse> findAllByInstitution(
            NursingInstitution institution, List<RecruitmentStatus> recruitmentStatus, Pageable pageable);

    // TODO : 서비스 계층에서 자동 매칭 카운팅하도록 로직 수정
    @Query(
            """
        SELECT new com.becareful.becarefulserver.domain.matching.dto.response.SocialWorkerRecruitmentResponse(
                   r,
                   e,
                   COUNT(a),
                   COUNT(a)
               )
          FROM Recruitment r
          JOIN r.elderly e
          LEFT JOIN Application a ON a.recruitment = r
         WHERE r.elderly.nursingInstitution = :institution
           AND r.recruitmentStatus IN :recruitmentStatus
           AND (r.title LIKE %:keyword% OR r.elderly.name LIKE %:keyword%)
         GROUP BY r, e
    """)
    Page<SocialWorkerRecruitmentResponse> searchByInstitutionAndElderlyNameOrRecruitmentTitle(
            NursingInstitution institution,
            List<RecruitmentStatus> recruitmentStatus,
            String keyword,
            Pageable pageable);

    @Query(
            """
        SELECT new com.becareful.becarefulserver.domain.matching.dto.response.SocialWorkerRecruitmentResponse(
                   r,
                   e,
                   COUNT(a),
                   COUNT(a)
               )
          FROM Recruitment r
          JOIN r.elderly e
          LEFT JOIN Application a ON a.recruitment = r
         WHERE r.elderly = :elderly
         GROUP BY r, e
    """)
    List<SocialWorkerRecruitmentResponse> getRecruitmentResponsesByElderly(Elderly elderly);

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
