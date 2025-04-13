package com.becareful.becarefulserver.domain.association.service;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.dto.response.AssociationMyResponse;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AssociationService {

    private final AuthUtil authUtil;
    private final SocialWorkerRepository socialWorkerRepository;

    public AssociationMyResponse getMyAssociation() {
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();
        Association association = currentSocialWorker.getAssociation();

        int associationMemberCount = socialWorkerRepository.countByAssociation(association);

        return AssociationMyResponse.from(association, associationMemberCount);
    }
}
