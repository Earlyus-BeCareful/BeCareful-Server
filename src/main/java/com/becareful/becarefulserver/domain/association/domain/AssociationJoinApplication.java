package com.becareful.becarefulserver.domain.association.domain;

import com.becareful.becarefulserver.domain.association.domain.vo.AssociationJoinApplicationStatus;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@RequiredArgsConstructor
public class AssociationJoinApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "association_id")
    private Association association;

    @OneToOne
    @JoinColumn(name = "social_worker_id")
    private SocialWorker socialWorker;

    @Enumerated(EnumType.STRING)
    private AssociationRank associationRank;

    @Enumerated(EnumType.STRING)
    private AssociationJoinApplicationStatus status;

    @Builder(access = AccessLevel.PRIVATE)
    private AssociationJoinApplication(
            Association association,
            SocialWorker socialWorker,
            AssociationRank associationRank,
            AssociationJoinApplicationStatus status) {
        this.association = association;
        this.socialWorker = socialWorker;
        this.associationRank = associationRank;
        this.status = status;
    }

    public static AssociationJoinApplication create(
            Association association,
            SocialWorker socialWorker,
            AssociationRank associationRank,
            AssociationJoinApplicationStatus status) {
        return AssociationJoinApplication.builder()
                .association(association)
                .socialWorker(socialWorker)
                .associationRank(associationRank)
                .status(status)
                .build();
    }

    public void approve() {
        this.status = AssociationJoinApplicationStatus.APPROVED;
    }

    public void reject() {
        this.status = AssociationJoinApplicationStatus.REJECTED;
    }
}
