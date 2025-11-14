package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TextChat extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    long id;

    String text;

    @Builder
    private TextChat(String text)
    {
        this.text = text;
    }

    public static TextChat create(String text)
    {
        return TextChat.builder()
                .text(text)
                .build();
    }
}

