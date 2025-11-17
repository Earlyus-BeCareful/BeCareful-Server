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
    private TextChat(ChatRoom chatRoom, String text) {
        super(chatRoom);
        this.text = text;
    }

    public static TextChat create(ChatRoom chatRoom, String text) {
        return TextChat.builder().chatRoom(chatRoom).text(text).build();
    }
}
