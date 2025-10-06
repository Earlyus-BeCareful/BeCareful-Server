package com.becareful.becarefulserver.domain.socialworker.repository;

import com.becareful.becarefulserver.domain.nursing_institution.domain.NursingInstitution;
import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ElderlyRepository extends JpaRepository<Elderly, Long> {

    List<Elderly> findAllByNursingInstitution(NursingInstitution nursingInstitution);

    List<Elderly> findByNursingInstitutionAndNameContaining(NursingInstitution nursingInstitution, String name);

    // TODO : 모집 공고가 없는 어르신 = 한번도 모집 공고를 올린 적 없는 어르신 vs '모집중' 인 공고가 없는 어르신
    // TODO : 모집 완료 이후, 계약이 종료되어 (계약 기간의 명시 필요 여부 검토) 더 이상 근무를 안하는 경우, 과거의 '모집 완료' 와 현재 근무중인 '모집 완료' 의 구분이 필요
    @Query(
            """
        SELECT e
          FROM Elderly e
         WHERE e.nursingInstitution = :institution
           AND NOT EXISTS (
               SELECT r.id
                 FROM Recruitment r
                WHERE r.elderly = e
           )
    """)
    Page<Elderly> findAllWaitingMatching(NursingInstitution institution, Pageable pageable);
}
