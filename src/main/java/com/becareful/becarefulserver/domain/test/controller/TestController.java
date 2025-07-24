package com.becareful.becarefulserver.domain.test.controller;

import com.becareful.becarefulserver.domain.caregiver.domain.Caregiver;
import com.becareful.becarefulserver.domain.caregiver.repository.CaregiverRepository;
import com.becareful.becarefulserver.domain.socialworker.domain.SocialWorker;
import com.becareful.becarefulserver.domain.socialworker.repository.SocialWorkerRepository;
import com.becareful.becarefulserver.global.exception.ErrorMessage;
import com.becareful.becarefulserver.global.exception.exception.CaregiverException;
import com.becareful.becarefulserver.global.exception.exception.SocialWorkerException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static com.becareful.becarefulserver.global.exception.ErrorMessage.CAREGIVER_NOT_EXISTS;
import static com.becareful.becarefulserver.global.exception.ErrorMessage.SOCIAL_WORKER_NOT_EXISTS;

@RestController
@RequiredArgsConstructor
@Tag(name = "TEST API", description = "개발 환경에서만 사용하는 테스트 API 입니다.")
@RequestMapping("/test")
public class TestController {

    private final SocialWorkerRepository socialWorkerRepository;
    private final CaregiverRepository caregiverRepository;

    @DeleteMapping("/social-worker")
    public ResponseEntity<Void> deleteSocialWorker(@RequestParam String phoneNumber) {
        SocialWorker socialWorker = socialWorkerRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new SocialWorkerException(SOCIAL_WORKER_NOT_EXISTS));
        socialWorkerRepository.delete(socialWorker);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/caregiver")
    public ResponseEntity<Void> deleteCaregiver(@RequestParam String phoneNumber) {
        Caregiver caregiver = caregiverRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new CaregiverException(CAREGIVER_NOT_EXISTS));
        caregiverRepository.delete(caregiver);
        return ResponseEntity.noContent().build();
    }
}
