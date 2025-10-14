package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.common.domain.BaseEntity;
import com.becareful.becarefulserver.domain.matching.domain.Contract;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long seq;

    private String content;

    @JoinColumn(name = "contract_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Contract contract;

    @JoinColumn(name = "social_worker_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private SocialWorker socialWorker;

    @JoinColumn(name = "caregiver_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Caregiver caregiver;

    @JoinColumn(name = "chat_room_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    public static ChatMessage createSocialWorkerMessage(
            Long seq, String content, SocialWorker socialWorker, Contract contract, ChatRoom chatRoom) {
        return ChatMessage.builder()
                .seq(seq)
                .content(content)
                .contract(contract)
                .socialWorker(socialWorker)
                .chatRoom(chatRoom)
                .build();
    }

    @Builder(access = AccessLevel.PRIVATE)
    private ChatMessage(
            Long seq,
            String content,
            Contract contract,
            SocialWorker socialWorker,
            Caregiver caregiver,
            ChatRoom chatRoom) {
        this.seq = seq;
        this.content = content;
        this.contract = contract;
        this.socialWorker = socialWorker;
        this.caregiver = caregiver;
        this.chatRoom = chatRoom;
    }
}
