package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.matching.domain.Application;
import com.becareful.becarefulserver.domain.matching.domain.ApplicationStatus;
import com.becareful.becarefulserver.domain.matching.domain.Recruitment;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {

    @Query(
            """
        SELECT a
          FROM Application a
         WHERE a.workApplication.caregiver = :caregiver
           AND a.applicationStatus IN :applicationStatuses
           AND (
                 (:isShouldBeRecruiting = TRUE AND a.recruitment.recruitmentStatus = com.becareful.becarefulserver.domain.matching.domain.RecruitmentStatus.모집중)
                 OR
                 (:isShouldBeRecruiting = FALSE AND a.recruitment.recruitmentStatus <> com.becareful.becarefulserver.domain.matching.domain.RecruitmentStatus.모집중)
               )
    """)
    List<Application> findAllByCaregiverAndApplicationStatusIn(
            Caregiver caregiver, List<ApplicationStatus> applicationStatuses, Boolean isShouldBeRecruiting);

    @Query(
            """
        SELECT a
          FROM Application a
         WHERE a.workApplication.caregiver = :caregiver
           AND a.recruitment = :recruitment
    """)
    Optional<Application> findByCaregiverAndRecruitment(Caregiver caregiver, Recruitment recruitment);

    List<Application> findAllByRecruitment(Recruitment recruitment);
}
