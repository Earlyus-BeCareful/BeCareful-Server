package com.becareful.becarefulserver.domain.community.repository;

import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostBoardRepository extends JpaRepository<PostBoard, Long> {}
