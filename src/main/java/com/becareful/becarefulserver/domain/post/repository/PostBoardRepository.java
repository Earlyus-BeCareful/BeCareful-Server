package com.becareful.becarefulserver.domain.post.repository;

import com.becareful.becarefulserver.domain.post.domain.PostBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostBoardRepository extends JpaRepository<PostBoard, Long> {}
