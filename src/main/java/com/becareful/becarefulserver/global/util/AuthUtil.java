package com.becareful.becarefulserver.global.util;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.AssociationMember;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.chat.domain.vo.ChatSenderType;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.exception.exception.SocialWorkerException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final CaregiverRepository caregiverRepository;
    private final SocialWorkerRepository socialworkerRepository;
    private final CookieUtil cookieUtil;

    public Caregiver getLoggedInCaregiver() {
        String phoneNumber =
                SecurityContextHolder.getContext().getAuthentication().getName();
        return caregiverRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_NOT_EXISTS));
    }

    public SocialWorker getLoggedInSocialWorker() {
        String phoneNumber =
                SecurityContextHolder.getContext().getAuthentication().getName();
        return socialworkerRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new SocialWorkerException(SOCIALWORKER_NOT_EXISTS));
    }

    public ChatSenderType getLoggedInChatSenderType() {
        String phoneNumber =
                SecurityContextHolder.getContext().getAuthentication().getName();
        Caregiver caregiver = caregiverRepository
                .findByPhoneNumber(phoneNumber).orElse(null);

        if (caregiver == null) {
            socialworkerRepository
                    .findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new SocialWorkerException(SOCIALWORKER_NOT_EXISTS));
            return ChatSenderType.SOCIAL_WORKER;

        }
        return ChatSenderType.SOCIAL_WORKER;
    }

    public AssociationMember getLoggedInAssociationMember() {
        String phoneNumber =
                SecurityContextHolder.getContext().getAuthentication().getName();
        SocialWorker socialWorker = socialworkerRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new SocialWorkerException(SOCIALWORKER_NOT_EXISTS));

        if (socialWorker.getAssociationMember() == null) {
            throw new SocialWorkerException(ASSOCIATION_MEMBER_NOT_EXISTS);
        }

        return socialWorker.getAssociationMember();
    }

    public void logout(HttpServletResponse response) {
        response.addCookie(cookieUtil.deleteCookie("AccessToken"));
        response.addCookie(cookieUtil.deleteCookie("RefreshToken"));
        SecurityContextHolder.clearContext();
    }
}
