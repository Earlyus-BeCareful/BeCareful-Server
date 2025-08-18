package com.becareful.becarefulserver.domain.test.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.*;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.exception.exception.SocialWorkerException;
import com.becareful.becarefulserver.global.properties.JwtProperties;
import com.becareful.becarefulserver.global.util.CookieUtil;
import com.becareful.becarefulserver.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TestService {

    private final SocialWorkerRepository socialWorkerRepository;
    private final CaregiverRepository caregiverRepository;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final JwtProperties jwtProperties;

    @Transactional
    public void deleteSocialWorker(String phoneNumber) {
        SocialWorker socialWorker = socialWorkerRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new SocialWorkerException(SOCIAL_WORKER_NOT_EXISTS));
        socialWorkerRepository.delete(socialWorker);
    }

    @Transactional
    public void deleteCaregiver(String phoneNumber) {
        Caregiver caregiver = caregiverRepository
                .findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new SocialWorkerException(CAREGIVER_NOT_EXISTS));
        caregiverRepository.delete(caregiver);
    }

    public void tokenSetting(String phoneNumber, HttpServletResponse response) {
        SocialWorker socialWorker =
                socialWorkerRepository.findByPhoneNumber(phoneNumber).orElse(null);

        if (socialWorker == null) {
            caregiverRepository
                    .findByPhoneNumber(phoneNumber)
                    .orElseThrow(() -> new CaregiverException(CAREGIVER_NOT_EXISTS));
        }

        String institutionRank =
                socialWorker == null ? null : socialWorker.getInstitutionRank().toString();
        String associationRank =
                socialWorker == null ? null : socialWorker.getAssociationRank().toString();

        String accessToken = jwtUtil.createAccessToken(phoneNumber, institutionRank, associationRank);
        String refreshToken = jwtUtil.createRefreshToken(phoneNumber);

        response.addCookie(
                cookieUtil.createCookie("AccessToken", accessToken, jwtProperties.getAccessTokenExpiry())); // 24시간
        response.addCookie(
                cookieUtil.createCookie("RefreshToken", refreshToken, jwtProperties.getRefreshTokenExpiry())); // 일주일
    }
}
