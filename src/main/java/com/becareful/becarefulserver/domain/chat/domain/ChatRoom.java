package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "matching_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Matching matching;

    public static ChatRoom create(Matching matching) {
        return ChatRoom.builder().matching(matching).build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ChatRoom(Matching matching) {
        this.matching = matching;
    }
}
