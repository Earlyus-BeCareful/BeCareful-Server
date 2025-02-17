package com.becareful.becarefulserver.domain.caregiver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.becareful.becarefulserver.domain.caregiver.domain.Career;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;

import java.util.Optional;

public interface CareerRepository extends JpaRepository<Career, Long> {

    Optional<Career> findByCaregiver(Caregiver caregiver);
}
