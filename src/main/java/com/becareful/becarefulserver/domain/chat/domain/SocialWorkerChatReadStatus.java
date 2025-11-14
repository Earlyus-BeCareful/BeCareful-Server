package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import jakarta.persistence.*;
import java.time.*;
import lombok.*;
import org.hibernate.annotations.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialWorkerChatReadStatus extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime lastReadAt;

    @JoinColumn(name = "social_worker_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SocialWorker socialWorker;

    @JoinColumn(name = "chat_room_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatRoom chatRoom;

    @Builder(access = AccessLevel.PRIVATE)
    private SocialWorkerChatReadStatus(SocialWorker socialWorker, ChatRoom chatRoom) {
        this.socialWorker = socialWorker;
        this.chatRoom = chatRoom;
        this.lastReadAt = LocalDateTime.now();
    }

    public static SocialWorkerChatReadStatus create(SocialWorker socialWorker, ChatRoom chatRoom) {
        return SocialWorkerChatReadStatus.builder()
                .socialWorker(socialWorker)
                .chatRoom(chatRoom)
                .build();
    }

    public void updateLastReadAt() {
        this.lastReadAt = LocalDateTime.now();
    }
}
