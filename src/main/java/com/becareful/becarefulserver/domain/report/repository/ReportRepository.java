package com.becareful.becarefulserver.domain.report.repository;

import com.becareful.becarefulserver.domain.report.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {}
