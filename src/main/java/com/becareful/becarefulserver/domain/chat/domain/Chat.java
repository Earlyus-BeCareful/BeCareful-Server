package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "chat_type")
public class Chat extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(name = "chat_room_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    private ChatSenderType senderType;

    protected Chat(ChatRoom chatRoom, ChatSenderType senderType) {
        this.chatRoom = chatRoom;
        this.senderType = senderType;
    }
}
