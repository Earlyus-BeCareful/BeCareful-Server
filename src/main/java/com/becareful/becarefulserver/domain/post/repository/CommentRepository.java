package com.becareful.becarefulserver.domain.post.repository;

import com.becareful.becarefulserver.domain.post.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {}
