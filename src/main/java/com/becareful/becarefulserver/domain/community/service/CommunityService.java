package com.becareful.becarefulserver.domain.community.service;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationJoinApplication;
import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.association.dto.response.*;
import com.becareful.becarefulserver.domain.association.repository.AssociationJoinApplicationRepository;
import com.becareful.becarefulserver.domain.association.repository.AssociationMemberRepository;
import com.becareful.becarefulserver.domain.chat.service.*;
import com.becareful.becarefulserver.domain.community.dto.response.*;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.properties.CookieProperties;
import com.becareful.becarefulserver.global.properties.JwtProperties;
import com.becareful.becarefulserver.global.util.AuthUtil;
import com.becareful.becarefulserver.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final SocialWorkerChatService chatService;
    private final AuthUtil authUtil;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final CookieProperties cookieProperties;
    private final AssociationJoinApplicationRepository associationMembershipRequestRepository;
    private final AssociationMemberRepository associationMemberRepository;

    public CommunityAccessResponse getCommunityAccess(HttpServletResponse httpServletResponse) {
        SocialWorker socialWorker = authUtil.getLoggedInSocialWorker();
        AssociationMember associationMember = socialWorker.getAssociationMember();

        Optional<AssociationJoinApplication> requestOpt =
                associationMembershipRequestRepository.findBySocialWorker(socialWorker);

        if (associationMember != null) { // 가입된 회원인 경우
            Association association = associationMember.getAssociation();
            int associationMemberCount = associationMemberRepository.countByAssociation(association);

            if (requestOpt.isPresent()) {
                associationMembershipRequestRepository.delete(requestOpt.get());
                return CommunityAccessResponse.approved(associationMember, associationMemberCount);
            }

            return CommunityAccessResponse.alreadyApproved(associationMember, associationMemberCount);
        }

        return requestOpt // 가입된 회원이 아닌 경우
                .map(request -> {
                    String associationName = request.getAssociation().getName();

                    switch (request.getStatus()) {
                        case REJECTED -> {
                            associationMembershipRequestRepository.delete(request);
                            return CommunityAccessResponse.rejected(associationName);
                        }
                        case PENDING -> {
                            return CommunityAccessResponse.pending(associationName);
                        }
                        default -> throw new IllegalStateException(
                                "Unexpected community access status: " + request.getStatus());
                    }
                })
                .orElseGet(CommunityAccessResponse::notApplied);
    }

    @Transactional(readOnly = true)
    public CommunityHomeBasicInfoResponse getCommunityHomeInfo() {
        AssociationMember currentMember = authUtil.getLoggedInAssociationMember();

        boolean hasNewChat = chatService.checkNewChat();
        Association association = currentMember.getAssociation();
        int associationMemberCount = associationMemberRepository.countByAssociation(association);

        AssociationMyResponse associationInfo = AssociationMyResponse.from(association, associationMemberCount);
        return CommunityHomeBasicInfoResponse.of(hasNewChat, associationInfo);
    }
}
