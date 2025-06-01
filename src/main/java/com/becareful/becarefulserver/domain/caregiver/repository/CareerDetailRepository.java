package com.becareful.becarefulserver.domain.caregiver.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareerDetailRepository extends JpaRepository<CareerDetail, Long> {

    void deleteAllByCareer(Career career);

    List<CareerDetail> findAllByCareer(Career career);
}
