package com.becareful.becarefulserver.domain.community.repository;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.community.domain.BoardType;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostBoardRepository extends JpaRepository<PostBoard, Long> {

    Optional<PostBoard> findByBoardTypeAndAssociation(BoardType boardType, Association association);
}
