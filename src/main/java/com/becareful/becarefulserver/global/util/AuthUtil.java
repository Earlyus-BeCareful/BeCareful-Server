package com.becareful.becarefulserver.global.util;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_NOT_EXISTS_WITH_PHONE_NUMBER;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.SOCIALWORKER_NOT_EXISTS;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialworkerRepository;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.exception.exception.SocialworkerException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final CaregiverRepository caregiverRepository;
    private final SocialworkerRepository socialworkerRepository;

    public Caregiver getLoggedInCaregiver() {
        String phoneNumber = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return caregiverRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_NOT_EXISTS_WITH_PHONE_NUMBER));
    }

    public SocialWorker getLoggedInSocialWorker() {
        String phoneNumber = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return socialworkerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new SocialworkerException(SOCIALWORKER_NOT_EXISTS));
    }
}
