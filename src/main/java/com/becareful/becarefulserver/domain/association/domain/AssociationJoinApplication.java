package com.becareful.becarefulserver.domain.association.domain;

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

    private boolean isAgreedToTerms;

    private boolean isAgreedToCollectPersonalInfo;

    private boolean isAgreedToReceiveMarketingInfo;

    @Builder(access = AccessLevel.PRIVATE)
    private AssociationJoinApplication(
            Association association,
            SocialWorker socialWorker,
            AssociationRank associationRank,
            AssociationJoinApplicationStatus status,
            boolean isAgreedToTerms,
            boolean isAgreedToCollectPersonalInfo,
            boolean isAgreedToReceiveMarketingInfo) {
        this.association = association;
        this.socialWorker = socialWorker;
        this.associationRank = associationRank;
        this.status = status;
        this.isAgreedToTerms = isAgreedToTerms;
        this.isAgreedToCollectPersonalInfo = isAgreedToCollectPersonalInfo;
        this.isAgreedToReceiveMarketingInfo = isAgreedToReceiveMarketingInfo;
    }

    public static AssociationJoinApplication create(
            SocialWorker socialWorker,
            Association association,
            AssociationRank associationRank,
            Boolean isAgreedToReceiveMarketingInfo) {
        return AssociationJoinApplication.builder()
                .association(association)
                .socialWorker(socialWorker)
                .associationRank(associationRank)
                .status(AssociationJoinApplicationStatus.PENDING)
                .isAgreedToReceiveMarketingInfo(isAgreedToReceiveMarketingInfo)
                .isAgreedToTerms(true)
                .isAgreedToCollectPersonalInfo(true)
                .build();
    }

    public void approve() {
        this.status = AssociationJoinApplicationStatus.APPROVED;
    }

    public void reject() {
        this.status = AssociationJoinApplicationStatus.REJECTED;
    }
}
