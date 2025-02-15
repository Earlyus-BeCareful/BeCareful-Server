package com.becareful.becarefulserver.global.util;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_NOT_EXISTS_WITH_PHONE_NUMBER;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final CaregiverRepository caregiverRepository;

    public Caregiver getLoggedInCaregiver() {
        String phoneNumber = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return caregiverRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_NOT_EXISTS_WITH_PHONE_NUMBER));
    }
}
