package com.becareful.becarefulserver.domain.community.domain.vo;

import com.becareful.becarefulserver.global.exception.ErrorMessage;
import com.becareful.becarefulserver.global.exception.exception.DomainException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommunityAgreement {

    private boolean isAgreedToTerms;

    private boolean isAgreedToCollectPersonalInfo;

    private boolean isAgreedToReceiveMarketingInfo;

    public static CommunityAgreement of(
            boolean isAgreedToTerms, boolean isAgreedToCollectPersonalInfo, boolean isAgreedToReceiveMarketingInfo) {
        if (!isAgreedToTerms || !isAgreedToCollectPersonalInfo) {
            throw new DomainException(ErrorMessage.COMMUNITY_REQUIRED_AGREEMENT_NOT_AGREED);
        }

        return new CommunityAgreement(true, true, isAgreedToReceiveMarketingInfo);
    }
}
