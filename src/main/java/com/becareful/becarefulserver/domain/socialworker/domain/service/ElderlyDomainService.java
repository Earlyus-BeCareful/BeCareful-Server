package com.becareful.becarefulserver.domain.socialworker.domain.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.ELDERLY_DIFFERENT_INSTITUTION;

import com.becareful.becarefulserver.domain.socialworker.domain.Elderly;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.global.exception.exception.ElderlyException;
import org.springframework.stereotype.Component;

@Component
public class ElderlyDomainService {

    public void validateElderlyAndSocialWorkerInstitution(Elderly elderly, SocialWorker socialWorker) {
        if (!elderly.getNursingInstitution().equals(socialWorker.getNursingInstitution())) {
            throw new ElderlyException(ELDERLY_DIFFERENT_INSTITUTION);
        }
    }
}
