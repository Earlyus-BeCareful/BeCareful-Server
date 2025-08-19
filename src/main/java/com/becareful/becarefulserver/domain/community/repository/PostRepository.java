package com.becareful.becarefulserver.domain.community.repository;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.community.domain.Post;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(
            """
        select p
          from Post p
         where p.board.association = :association
           and p.board = :postBoard
""")
    Page<Post> findAllByBoardAndAssociation(PostBoard postBoard, Association association, Pageable pageable);

    @Query(
            """
        select p
          from Post p
         where p.isImportant = true
           and p.board.readableRank = :readableRank
           and p.board.association = :association
    """)
    Page<Post> findAllReadableImportantPosts(AssociationRank readableRank, Association association, Pageable pageable);
}
