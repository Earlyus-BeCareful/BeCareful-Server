package com.becareful.becarefulserver.domain.community.domain;

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

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostBoard extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BoardType boardType;

    @Enumerated(EnumType.STRING)
    private AssociationRank readableRank;

    @Enumerated(EnumType.STRING)
    private AssociationRank writableRank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "association_id")
    private Association association;

    @Builder(access = AccessLevel.PRIVATE)
    private PostBoard(
            BoardType boardType, AssociationRank readableRank, AssociationRank writableRank, Association association) {
        this.boardType = boardType;
        this.readableRank = readableRank;
        this.writableRank = writableRank;
        this.association = association;
    }

    public static PostBoard create(
            BoardType boardType, AssociationRank readableRank, AssociationRank writableRank, Association association) {
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
    public void validateReadableFor(SocialWorker socialWorker) {
        validateAssociationOf(socialWorker);
        validateReadableRankFor(socialWorker);
    }

    public void validateWritableFor(SocialWorker socialWorker) {
        validateAssociationOf(socialWorker);
        validateWritableRankFor(socialWorker);
    }

    private void validateReadableRankFor(SocialWorker socialWorker) {
        if (socialWorker.getAssociationRank().isLowerThan(readableRank)) {
            throw new PostBoardException(POST_BOARD_NOT_READABLE);
        }
    }

    private void validateWritableRankFor(SocialWorker socialWorker) {
        if (socialWorker.getAssociationRank().isLowerThan(writableRank)) {
            throw new PostBoardException(POST_BOARD_NOT_WRITABLE);
        }
    }

    private void validateAssociationOf(SocialWorker socialWorker) {
        if (!socialWorker.getAssociation().equals(association)) {
            throw new PostBoardException(POST_BOARD_NOT_MY_ASSOCIATION);
        }
    }
}
