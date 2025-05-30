/* (C)2025 */
package com.becareful.becarefulserver.domain.community.repository;

import com.becareful.becarefulserver.domain.community.domain.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {}
