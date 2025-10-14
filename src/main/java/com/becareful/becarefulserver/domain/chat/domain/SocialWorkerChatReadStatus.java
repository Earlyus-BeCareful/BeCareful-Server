package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
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

    private Long lastReadSeq;

    @JoinColumn(name = "social_worker_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SocialWorker socialWorker;

    @JoinColumn(name = "chat_room_id")
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ChatRoom chatRoom;

    @Builder(access = AccessLevel.PRIVATE)
    private SocialWorkerChatReadStatus(ChatRoom chatRoom, Long lastReadSeq, SocialWorker socialWorker) {
        this.chatRoom = chatRoom;
        this.lastReadSeq = lastReadSeq;
        this.socialWorker = socialWorker;
    }

    public static SocialWorkerChatReadStatus create(ChatRoom chatRoom, SocialWorker socialWorker) {
        return SocialWorkerChatReadStatus.builder()
                .chatRoom(chatRoom)
                .socialWorker(socialWorker)
                .lastReadSeq(1L)
                .build();
    }

    public void updateLastReadSeq(Long lastReadSeq) {
        this.lastReadSeq = lastReadSeq;
    }
}
