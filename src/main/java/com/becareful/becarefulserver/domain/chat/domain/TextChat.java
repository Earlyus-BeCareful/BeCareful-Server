package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("TEXT")
public class TextChat extends Chat {

    private String text;

    @Builder
    private TextChat(ChatRoom chatRoom, ChatSenderType senderType, String text) {
        super(chatRoom, senderType);
        this.text = text;
    }

    public static TextChat create(ChatRoom chatRoom, ChatSenderType senderType, String text) {
        return TextChat.builder()
                .chatRoom(chatRoom)
                .senderType(senderType)
                .text(text)
                .build();
    }
}
