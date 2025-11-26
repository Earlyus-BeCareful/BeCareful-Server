package com.becareful.becarefulserver.global.util;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.association.domain.*;
import com.becareful.becarefulserver.domain.caregiver.domain.*;
import com.becareful.becarefulserver.domain.caregiver.repository.*;
import com.becareful.becarefulserver.domain.socialworker.domain.*;
import com.becareful.becarefulserver.domain.socialworker.repository.*;
import com.becareful.becarefulserver.global.exception.exception.*;
import jakarta.servlet.http.*;
import lombok.*;
import org.springframework.security.core.context.*;
import org.springframework.stereotype.*;

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
                .orElseThrow(() -> new SocialWorkerException(SOCIAL_WORKER_NOT_EXISTS));
    }

    public AssociationMember getLoggedInAssociationMember() {
        String phoneNumber =
                SecurityContextHolder.getContext().getAuthentication().getName();
        SocialWorker socialWorker = socialworkerRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new SocialWorkerException(SOCIAL_WORKER_NOT_EXISTS));

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
