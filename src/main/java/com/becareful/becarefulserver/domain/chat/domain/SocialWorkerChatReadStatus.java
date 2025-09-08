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

    private LocalDateTime lastReadAt;

    @JoinColumn(name = "social_worker_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SocialWorker socialWorker;

    @JoinColumn(name = "matching_id")
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Matching matching;

    @Builder(access = AccessLevel.PRIVATE)
    private SocialWorkerChatReadStatus(SocialWorker socialWorker, Matching matching) {
        this.socialWorker = socialWorker;
        this.matching = matching;
        this.lastReadAt = LocalDateTime.now();
    }

    public static SocialWorkerChatReadStatus create(SocialWorker socialWorker, Matching matching) {
        return SocialWorkerChatReadStatus.builder()
                .socialWorker(socialWorker)
                .matching(matching)
                .build();
    }

    public void updateLastReadAt() {
        this.lastReadAt = LocalDateTime.now();
    }
}
