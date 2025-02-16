package com.becareful.becarefulserver.domain.caregiver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplicationWorkLocation;
import com.becareful.becarefulserver.domain.work_location.domain.WorkLocation;

import java.util.List;

public interface WorkApplicationWorkLocationRepository extends
        JpaRepository<WorkApplicationWorkLocation, Long> {

    void deleteAllByWorkApplication(WorkApplication workApplication);
    List<WorkApplicationWorkLocation> findAllByWorkApplication(WorkApplication workApplication);

    @Query("SELECT w "
            + "FROM WorkApplicationWorkLocation w "
            + "WHERE w.workApplication.isActive")
    List<WorkApplicationWorkLocation> findAllActiveWorkApplication();
}
