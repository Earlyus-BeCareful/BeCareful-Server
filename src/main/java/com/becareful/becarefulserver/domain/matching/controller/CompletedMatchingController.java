package com.becareful.becarefulserver.domain.matching.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.becareful.becarefulserver.domain.matching.service.CompletedMatchingService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/complete-matching")
@RequiredArgsConstructor
public class CompletedMatchingController {

    private final CompletedMatchingService completedMatchingService;

    @PostMapping
    public ResponseEntity<Void> createCompleteMatching(@RequestParam(name = "contractId") Long contractId) {
        completedMatchingService.createCompletedMatching(contractId);
        return ResponseEntity.ok().build();
    }
}
