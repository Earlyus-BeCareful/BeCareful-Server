package com.becareful.becarefulserver.domain.caregiver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;

import java.util.Optional;

public interface WorkApplicationRepository extends JpaRepository<WorkApplication, Long> {

    Optional<WorkApplication> findByCaregiver(Caregiver caregiver);
}
