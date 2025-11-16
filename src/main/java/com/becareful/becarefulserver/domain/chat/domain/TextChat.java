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

    @Builder(access = AccessLevel.PRIVATE)
    private TextChat(ChatRoom chatRoom, String text)
    {
        create(chatRoom, ChatType.TEXT);
        this.setChatType(ChatType.TEXT);
        this.text = text;
    }

    public static TextChat create(ChatRoom chatRoom, String text)
    {
        return TextChat.builder()
                .chatRoom(chatRoom)
                .text(text)
                .build();
    }
}

