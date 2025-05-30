package com.becareful.becarefulserver.domain.caregiver.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplicationWorkLocation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkApplicationWorkLocationRepository extends JpaRepository<WorkApplicationWorkLocation, Long> {

    void deleteAllByWorkApplication(WorkApplication workApplication);

    List<WorkApplicationWorkLocation> findAllByWorkApplication(WorkApplication workApplication);
}
