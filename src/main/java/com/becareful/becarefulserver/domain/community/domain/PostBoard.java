package com.becareful.becarefulserver.domain.community.domain;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.POST_BOARD_NOT_READABLE;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import com.becareful.becarefulserver.global.exception.exception.PostBoardException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BoardType boardType;

    @Enumerated(EnumType.STRING)
    private AssociationRank readableRank;

    @Enumerated(EnumType.STRING)
    private AssociationRank writableRank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "association_id")
    private Association association;

    @Builder(access = AccessLevel.PRIVATE)
    private PostBoard(BoardType boardType, AssociationRank readableRank, AssociationRank writableRank, Association association) {
        this.boardType = boardType;
        this.readableRank = readableRank;
        this.writableRank = writableRank;
        this.association = association;
    }

    public static PostBoard create(BoardType boardType, AssociationRank readableRank, AssociationRank writableRank, Association association) {
        return PostBoard.builder()
                .boardType(boardType)
                .readableRank(readableRank)
                .writableRank(writableRank)
                .association(association)
                .build();
    }

    /**
     * 검증 로직
     */
    public void validateReadableFor(SocialWorker currentMember) {
        if (!this.getReadableRank().equals(currentMember.getAssociationRank())) {
            throw new PostBoardException(POST_BOARD_NOT_READABLE);
        }
    }
}
