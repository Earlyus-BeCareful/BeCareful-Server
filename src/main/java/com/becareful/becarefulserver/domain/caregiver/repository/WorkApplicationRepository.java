package com.becareful.becarefulserver.domain.caregiver.repository;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.domain.WorkApplication;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WorkApplicationRepository extends JpaRepository<WorkApplication, Long> {

    Optional<WorkApplication> findByCaregiver(Caregiver caregiver);

    @Query("SELECT w " + "FROM WorkApplication w " + "WHERE w.isActive")
    List<WorkApplication> findAllActiveWorkApplication();
}
