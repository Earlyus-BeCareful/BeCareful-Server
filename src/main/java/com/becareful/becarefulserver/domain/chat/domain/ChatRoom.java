package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatRoomStatus;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.matching.domain.Matching;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue()
    private Long id;

    @JoinColumn(name = "matching_id", nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private Matching matching;

    @Enumerated(EnumType.STRING)
    private ChatRoomStatus chatRoomStatus;

    @Builder(access = AccessLevel.PRIVATE)
    private ChatRoom(ChatRoomStatus chatRoomStatus, Matching matching) {
        this.chatRoomStatus = chatRoomStatus;
        this.matching = matching;
    }
}
