package com.becareful.becarefulserver.domain.auth.service;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.PASSWORD_NOT_MATCH;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.PHONE_NUMBER_NOT_EXISTS;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.becareful.becarefulserver.domain.auth.dto.request.LoginRequest;
import com.becareful.becarefulserver.domain.auth.dto.response.LoginResponse;
import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialworkerRepository;
import com.becareful.becarefulserver.global.exception.exception.AuthException;
import com.becareful.becarefulserver.global.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final CaregiverRepository caregiverRepository;
    private final SocialworkerRepository socialworkerRepository;
    private final JwtUtil jwtUtil;

    public LoginResponse loginCaregiver(LoginRequest request) {
        Caregiver caregiver = caregiverRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new AuthException(PHONE_NUMBER_NOT_EXISTS));

        validatePassword(request.password(), caregiver.getPassword());

        String accessToken = jwtUtil.generateToken(request.phoneNumber());
        return new LoginResponse(accessToken);
    }

    public LoginResponse loginSocialWorker(LoginRequest request) {
        SocialWorker socialworker = socialworkerRepository.findByPhoneNumber(request.phoneNumber())
                .orElseThrow(() -> new AuthException(PHONE_NUMBER_NOT_EXISTS));

        validatePassword(request.password(), socialworker.getPassword());

        String accessToken = jwtUtil.generateToken(request.phoneNumber());
        return new LoginResponse(accessToken);
    }

    private void validatePassword(String requestPassword, String encodedPassword) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(requestPassword, encodedPassword)) {
            throw new AuthException(PASSWORD_NOT_MATCH);
        }
    }
}
