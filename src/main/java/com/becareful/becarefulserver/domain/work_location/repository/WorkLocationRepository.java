package com.becareful.becarefulserver.domain.work_location.repository;

import com.becareful.becarefulserver.domain.common.vo.Location;
import com.becareful.becarefulserver.domain.work_location.domain.WorkLocation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkLocationRepository extends JpaRepository<WorkLocation, Long> {

    Optional<WorkLocation> findByLocation(Location location);
}
