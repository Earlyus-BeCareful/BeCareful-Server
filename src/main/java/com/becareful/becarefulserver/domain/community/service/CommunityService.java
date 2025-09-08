package com.becareful.becarefulserver.domain.community.service;

import com.becareful.becarefulserver.domain.association.domain.Association;
import com.becareful.becarefulserver.domain.association.domain.AssociationJoinApplication;
import com.becareful.becarefulserver.domain.association.dto.response.*;
import com.becareful.becarefulserver.domain.association.repository.AssociationJoinApplicationRepository;
import com.becareful.becarefulserver.domain.chat.service.*;
import com.becareful.becarefulserver.domain.community.dto.response.*;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.properties.CookieProperties;
import com.becareful.becarefulserver.global.properties.JwtProperties;
import com.becareful.becarefulserver.global.util.AuthUtil;
import com.becareful.becarefulserver.global.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.*;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final SocialWorkerChatService chatService;
    private final AuthUtil authUtil;
    private final SocialWorkerRepository socialWorkerRepository;
    private final AssociationJoinApplicationRepository associationMembershipRequestRepository;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final CookieProperties cookieProperties;

    public CommunityAccessResponse getCommunityAccess(HttpServletResponse httpServletResponse) {
        SocialWorker socialWorker = authUtil.getLoggedInSocialWorker();

        // jwt의 role과 DB의 role이 다른 경우 DB의 role로 업데이트

        List<String> grantedRoles = SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // 예: "ROLE_CHAIRMAN", "ROLE_NONE"
                .map(role -> role.replace("ROLE_", "")) // 예: "CHAIRMAN", "NONE"
                .toList();

        String dbAssociationRank = socialWorker.getAssociationRank().toString(); // 실제 DB 기준 최신 rank

        if (!grantedRoles.contains(dbAssociationRank)) {
            // JWT 재발급 필요
            updateJwtAndSecurityContext(
                    httpServletResponse,
                    socialWorker.getPhoneNumber(),
                    socialWorker.getInstitutionRank().toString(),
                    dbAssociationRank);
        }

        Association association = socialWorker.getAssociation();
        Optional<AssociationJoinApplication> requestOpt =
                associationMembershipRequestRepository.findBySocialWorker(socialWorker);

        if (association != null) { // 가입된 회원인 경우
            int associationMemberCount = socialWorkerRepository.countByAssociation(association);
            String associationName = association.getName();

            if (requestOpt.isPresent()) {
                associationMembershipRequestRepository.delete(requestOpt.get());
                return CommunityAccessResponse.approved(socialWorker, associationName, associationMemberCount);
            }

            return CommunityAccessResponse.alreadyApproved(socialWorker, associationMemberCount);
        }

        return requestOpt // 가입된 회원이 아닌 경우
                .map(request -> {
                    String associationName = request.getAssociation().getName();

                    switch (request.getStatus()) {
                        case REJECTED -> {
                            associationMembershipRequestRepository.delete(request);
                            return CommunityAccessResponse.rejected(socialWorker, associationName);
                        }
                        case PENDING -> {
                            return CommunityAccessResponse.pending(socialWorker, associationName);
                        }
                        default -> throw new IllegalStateException(
                                "Unexpected community access status: " + request.getStatus());
                    }
                })
                .orElseGet(() -> CommunityAccessResponse.notApplied(socialWorker));
    }

    @Transactional(readOnly = true)
    public CommunityHomeBasicInfoResponse getCommunityHomeInfo() {
        SocialWorker currentSocialWorker = authUtil.getLoggedInSocialWorker();

        boolean hasNewChat = chatService.checkNewChat();
        Association association = currentSocialWorker.getAssociation();
        int associationMemberCount = socialWorkerRepository.countByAssociation(association);

        AssociationMyResponse associationInfo = AssociationMyResponse.from(association, associationMemberCount);
        return CommunityHomeBasicInfoResponse.of(hasNewChat, associationInfo);
    }

    private void updateJwtAndSecurityContext(
            HttpServletResponse response, String phoneNumber, String institutionRank, String associationRank) {
        String accessToken = jwtUtil.createAccessToken(phoneNumber, institutionRank, associationRank);
        String refreshToken = jwtUtil.createRefreshToken(phoneNumber);

        response.addCookie(createCookie("AccessToken", accessToken, jwtProperties.getAccessTokenExpiry()));
        response.addCookie(createCookie("RefreshToken", refreshToken, jwtProperties.getRefreshTokenExpiry()));

        List<GrantedAuthority> authorities =
                List.of((GrantedAuthority) () -> institutionRank, (GrantedAuthority) () -> associationRank);

        Authentication auth = new UsernamePasswordAuthenticationToken(phoneNumber, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private Cookie createCookie(String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(maxAge);
        cookie.setSecure(cookieProperties.getCookieSecure());
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", cookieProperties.getCookieSameSite());
        return cookie;
    }
}
