package com.becareful.becarefulserver.domain.community.repository;

import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.Rank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByBoard(PostBoard postBoard, Pageable pageable);

    @Query("""
        select p
          from Post p
         where p.isImportant = true
           and p.board.readableRank = :readableRank
    """)
    Page<Post> findAllReadableImportantPosts(Rank readableRank, Pageable pageable);
}
