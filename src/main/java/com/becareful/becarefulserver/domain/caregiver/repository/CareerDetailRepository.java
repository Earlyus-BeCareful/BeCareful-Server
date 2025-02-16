package com.becareful.becarefulserver.domain.caregiver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.CareerDetail;

public interface CareerDetailRepository extends JpaRepository<CareerDetail, Long> {
    void deleteAllByCareer(Career career);
}
