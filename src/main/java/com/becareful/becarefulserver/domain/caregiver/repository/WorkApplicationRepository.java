package com.becareful.becarefulserver.domain.caregiver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplicationWorkLocation;

import java.util.List;
import java.util.Optional;

public interface WorkApplicationRepository extends JpaRepository<WorkApplication, Long> {

    Optional<WorkApplication> findByCaregiver(Caregiver caregiver);

    @Query("SELECT w "
            + "FROM WorkApplication w "
            + "WHERE w.isActive")
    List<WorkApplication> findAllActiveWorkApplication();
}
