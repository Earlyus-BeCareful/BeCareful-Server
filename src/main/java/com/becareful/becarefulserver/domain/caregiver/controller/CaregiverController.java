package com.becareful.becarefulserver.domain.caregiver.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.becareful.becarefulserver.domain.caregiver.dto.request.CaregiverCreateRequest;
import com.becareful.becarefulserver.domain.caregiver.service.CaregiverService;

import java.net.URI;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/caregiver")
public class CaregiverController {

    private final CaregiverService caregiverService;

    @PostMapping
    public ResponseEntity<Void> createCaregiver(@RequestBody CaregiverCreateRequest request) {
        Long id = caregiverService.saveCaregiver(request);
        return ResponseEntity.created(URI.create("/caregiver/" + id)).build();
    }
}
