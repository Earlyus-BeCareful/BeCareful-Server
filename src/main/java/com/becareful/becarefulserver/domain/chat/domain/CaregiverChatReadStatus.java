package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.common.domain.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;
import org.hibernate.annotations.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CaregiverChatReadStatus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long lastReadSeq;

    @JoinColumn(name = "chat_room_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatRoom chatRoom;

    @Builder(access = AccessLevel.PRIVATE)
    private CaregiverChatReadStatus(ChatRoom chatRoom, Long lastReadSeq) {
        this.chatRoom = chatRoom;
        this.lastReadSeq = lastReadSeq;
    }

    public static CaregiverChatReadStatus create(ChatRoom chatRoom, Long lastReadSeq) {
        return CaregiverChatReadStatus.builder()
                .chatRoom(chatRoom)
                .lastReadSeq(lastReadSeq)
                .build();
    }

    public void updateLastReadSeq(Long lastReadSeq) {
        this.lastReadSeq = lastReadSeq;
    }
}
