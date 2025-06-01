package com.becareful.becarefulserver.domain.association.domain;

import com.becareful.becarefulserver.domain.association.vo.AssociationJoinRequestStatus;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.domain.vo.AssociationRank;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@RequiredArgsConstructor
public class AssociationMembershipRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Association association;

    @OneToOne
    private SocialWorker socialWorker;

    @Enumerated(EnumType.STRING)
    private AssociationRank associationRank;

    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.PUBLIC)
    private AssociationJoinRequestStatus status;

    @Builder(access = AccessLevel.PRIVATE)
    private AssociationMembershipRequest(Association association, SocialWorker socialWorker, AssociationRank associationRank, AssociationJoinRequestStatus status){
        this.association = association;
        this.socialWorker = socialWorker;
        this.associationRank = associationRank;
        this.status = status;
    }

    public static AssociationMembershipRequest create(Association association, SocialWorker socialWorker, AssociationRank associationRank, AssociationJoinRequestStatus status){
        return AssociationMembershipRequest.builder()
                .association(association)
                .socialWorker(socialWorker)
                .associationRank(associationRank)
                .status(status)
                .build();
    }
}
