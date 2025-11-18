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
    private ChatRoomActiveStatus chatRoomActiveStatus;

    @Enumerated(EnumType.STRING)
    private ChatRoomContractStatus chatRoomContractStatus;

    @Builder(access = AccessLevel.PRIVATE)
    private ChatRoom(Recruitment recruitment) {
        this.chatRoomActiveStatus = ChatRoomActiveStatus.채팅가능;
        this.chatRoomContractStatus = ChatRoomContractStatus.근무조건조율중;
        this.recruitment = recruitment;
    }

    public static ChatRoom create(Recruitment recruitment) {
        return builder().recruitment(recruitment).build();
    }

    public void otherMatchingConfirmed() {
        this.chatRoomActiveStatus = ChatRoomActiveStatus.타매칭채용완료;
    }

    public void caregiverLeave() {
        this.chatRoomActiveStatus = ChatRoomActiveStatus.요양보호사탈퇴;
    }

    public void allSocialWorkerLeave() {
        this.chatRoomActiveStatus = ChatRoomActiveStatus.사회복지사전원탈퇴;
    }

    public void recruitmentClosed() {
        this.chatRoomActiveStatus = ChatRoomActiveStatus.공고마감;
    }

    public void acceptContract() {
        this.chatRoomContractStatus = ChatRoomContractStatus.근무조건동의;
    }

    public void confirmContract() {
        this.chatRoomContractStatus = ChatRoomContractStatus.채용완료;
    }
}
