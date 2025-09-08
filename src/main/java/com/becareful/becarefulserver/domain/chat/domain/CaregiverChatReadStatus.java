package com.becareful.becarefulserver.domain.chat.domain;

import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.common.domain.*;
import com.becareful.becarefulserver.domain.matching.domain.*;
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

    @JoinColumn(name = "matching_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Matching matching;

    @Builder(access = AccessLevel.PRIVATE)
    private CaregiverChatReadStatus(Caregiver caregiver, Matching matching) {
        this.caregiver = caregiver;
        this.matching = matching;
        this.lastReadAt = LocalDateTime.now();
    }

    public static CaregiverChatReadStatus create(Caregiver caregiver, Matching matching) {
        return CaregiverChatReadStatus.builder()
                .caregiver(caregiver)
                .matching(matching)
                .build();
    }

    public void updateLastReadAt() {
        this.lastReadAt = LocalDateTime.now();
    }
}
