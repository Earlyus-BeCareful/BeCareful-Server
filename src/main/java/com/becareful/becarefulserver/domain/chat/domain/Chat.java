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

    @Enumerated(EnumType.STRING)
    ChatType chatType;

    @Builder
    private Chat(ChatRoom chatRoom, ChatType chatType, long targetId) {
        this.chatRoom = chatRoom;
        this.chatType = chatType;
    }

    public static Chat create(ChatRoom chatRoom, ChatType chatType){
        return Chat.builder()
                .chatRoom(chatRoom)
                .chatType(chatType)
                .build();
    }

    protected void setChatType(ChatType chatType) {
        this.chatType = chatType;
    }
}
