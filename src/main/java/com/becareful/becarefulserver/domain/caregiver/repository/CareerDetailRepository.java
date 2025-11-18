package com.becareful.becarefulserver.domain.caregiver.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CareerDetailRepository extends JpaRepository<CareerDetail, Long> {

    void deleteAllByCareer(Career career);

    List<CareerDetail> findAllByCareer(Career career);

    @Query("""
        SELECT c
          FROM CareerDetail c
         WHERE c.career.caregiver = :caregiver
    """)
    List<CareerDetail> findAllByCaregiver(Caregiver caregiver);
}
