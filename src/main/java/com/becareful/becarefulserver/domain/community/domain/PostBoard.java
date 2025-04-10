package com.becareful.becarefulserver.domain.community.domain;

import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
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

    private String name;

    @Enumerated(EnumType.STRING)
    private AssociationRank readableRank;

    @Enumerated(EnumType.STRING)
    private AssociationRank writableRank;

    @Builder(access = AccessLevel.PRIVATE)
    private PostBoard(String name, AssociationRank readableRank, AssociationRank writableRank) {
        this.name = name;
        this.readableRank = readableRank;
        this.writableRank = writableRank;
    }

    public static PostBoard create(String name, AssociationRank readableRank, AssociationRank writableRank) {
        return PostBoard.builder()
                .name(name)
                .readableRank(readableRank)
                .writableRank(writableRank)
                .build();
    }
}
