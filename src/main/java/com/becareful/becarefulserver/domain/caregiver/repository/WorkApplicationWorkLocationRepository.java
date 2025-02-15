package com.becareful.becarefulserver.domain.caregiver.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplicationWorkLocation;

public interface WorkApplicationWorkLocationRepository extends
        JpaRepository<WorkApplicationWorkLocation, Long> {

    void deleteAllByWorkApplication(WorkApplication workApplication);
}
