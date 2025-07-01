package com.becareful.becarefulserver.global.util;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_NOT_EXISTS;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.SOCIALWORKER_NOT_EXISTS;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.exception.exception.SocialWorkerException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final CaregiverRepository caregiverRepository;
    private final SocialWorkerRepository socialworkerRepository;

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
}
