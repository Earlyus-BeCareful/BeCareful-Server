package com.becareful.becarefulserver.domain.community.repository;

import com.becareful.becarefulserver.domain.community.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {}
