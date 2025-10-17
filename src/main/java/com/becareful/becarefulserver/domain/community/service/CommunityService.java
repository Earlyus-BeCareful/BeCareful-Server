package com.becareful.becarefulserver.domain.community.service;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.dto.response.*;
import com.becareful.becarefulserver.domain.chat.service.*;
import com.becareful.becarefulserver.domain.community.dto.response.*;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final SocialWorkerChatService chatService;
    private final AuthUtil authUtil;
    private final SocialWorkerRepository socialWorkerRepository;

    @Transactional(readOnly = true)
    public CommunityHomeBasicInfoResponse getCommunityHomeInfo() {
        AssociationMember loggedInAssociationMember = authUtil.getLoggedInAssociationMember();

        boolean hasNewChat = chatService.checkNewChat();
        Association association = loggedInAssociationMember.getAssociation();
        int associationMemberCount = socialWorkerRepository.countByAssociation(association);

        AssociationMyResponse associationInfo = AssociationMyResponse.from(association, associationMemberCount);
        return CommunityHomeBasicInfoResponse.of(hasNewChat, associationInfo);
    }
}
