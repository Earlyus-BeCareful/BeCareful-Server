package com.becareful.becarefulserver.domain.community.repository;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.community.domain.BoardType;
import com.becareful.becarefulserver.domain.community.domain.PostBoard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostBoardRepository extends JpaRepository<PostBoard, Long> {

    Optional<PostBoard> findByBoardTypeAndAssociation(BoardType boardType, Association association);
}
