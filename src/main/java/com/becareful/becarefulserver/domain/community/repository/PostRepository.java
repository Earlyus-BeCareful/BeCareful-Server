package com.becareful.becarefulserver.domain.community.repository;

import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByBoard(PostBoard postBoard, Pageable pageable);
}
