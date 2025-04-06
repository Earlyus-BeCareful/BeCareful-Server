package com.becareful.becarefulserver.domain.community.domain;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.Rank;
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

    private String name;

    @Enumerated(EnumType.STRING)
    private Rank readableRank;

    @Enumerated(EnumType.STRING)
    private Rank writableRank;

    @Builder(access = AccessLevel.PRIVATE)
    private PostBoard(String name, Rank readableRank, Rank writableRank) {
        this.name = name;
        this.readableRank = readableRank;
        this.writableRank = writableRank;
    }

    public static PostBoard create(String name, Rank readableRank, Rank writableRank) {
        return PostBoard.builder()
                .name(name)
                .readableRank(readableRank)
                .writableRank(writableRank)
                .build();
    }
}
