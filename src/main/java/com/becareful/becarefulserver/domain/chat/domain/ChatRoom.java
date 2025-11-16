package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.chat.domain.vo.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatRoom extends BaseEntity {
    @Id
    @GeneratedValue()
    private Long id;

    @JoinColumn(name = "matching_id", nullable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private Recruitment recruitment;

    @Enumerated(EnumType.STRING)
    private ChatRoomActivateStatus chatRoomActivateStatus;

    @Enumerated(EnumType.STRING)
    private ChatRoomContractStatus chatRoomContractStatus;


    @Builder(access = AccessLevel.PRIVATE)
    private ChatRoom(Recruitment recruitment) {
        this.chatRoomActivateStatus = ChatRoomActivateStatus.채팅가능;
        this.chatRoomContractStatus = ChatRoomContractStatus.근무조건조율중;
        this.recruitment = recruitment;
    }

    public static ChatRoom create(Recruitment recruitment) {
        return builder()
                .recruitment(recruitment)
                .build();
    }

    public void updateStatusTo(ChatRoomActivateStatus chatRoomStatus) {
        this.chatRoomActivateStatus = chatRoomStatus;
    }
}
