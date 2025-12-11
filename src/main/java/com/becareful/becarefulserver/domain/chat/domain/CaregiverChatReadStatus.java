package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
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

    private LocalDateTime lastReadAt;

    @JoinColumn(name = "caregiver_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Caregiver caregiver;

    @JoinColumn(name = "chat_room_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatRoom chatRoom;

    @Builder(access = AccessLevel.PRIVATE)
    private CaregiverChatReadStatus(Caregiver caregiver, ChatRoom chatRoom) {
        this.caregiver = caregiver;
        this.chatRoom = chatRoom;
        this.lastReadAt = LocalDateTime.of(1000, 1, 1, 0, 0);
    }

    public static CaregiverChatReadStatus create(Caregiver caregiver, ChatRoom chatRoom) {
        return CaregiverChatReadStatus.builder()
                .caregiver(caregiver)
                .chatRoom(chatRoom)
                .build();
    }

    public void updateLastReadAt() {
        this.lastReadAt = LocalDateTime.now();
    }
}
