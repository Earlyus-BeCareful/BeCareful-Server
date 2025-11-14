package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.chat.domain.vo.ChatType;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat extends BaseEntity {
    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(name = "chat_room_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @Enumerated(EnumType.STRING)
    ChatType chatType;

    private long targetId;

    @Builder
    private Chat(ChatRoom chatRoom, ChatType chatType, long targetId) {
        this.chatRoom = chatRoom;
        this.chatType = chatType;
        this.targetId = targetId;
    }

    public static Chat create(ChatRoom chatRoom, ChatType chatType, long targetId){
        return Chat.builder()
                .chatRoom(chatRoom)
                .chatType(chatType)
                .targetId(targetId)
                .build();
    }
}
