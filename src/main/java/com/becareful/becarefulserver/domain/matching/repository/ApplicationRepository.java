package com.becareful.becarefulserver.domain.matching.repository;

import com.becareful.becarefulserver.domain.matching.domain.Application;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<Application, Integer> {}
