package com.becareful.becarefulserver.domain.socialworker.dto.response;

import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;

public record SocialWorkerMySettingResponse(boolean isAgreedToReceiveMarketingInfo) {
    public static SocialWorkerMySettingResponse from(SocialWorker socialWorker) {
        return new SocialWorkerMySettingResponse(socialWorker.isAgreedToReceiveMarketingInfo());
    }
}
