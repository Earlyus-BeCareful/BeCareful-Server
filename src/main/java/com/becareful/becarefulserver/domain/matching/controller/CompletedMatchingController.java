package com.becareful.becarefulserver.domain.matching.controller;

import com.becareful.becarefulserver.domain.matching.service.CompletedMatchingService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/complete-matching")
@RequiredArgsConstructor
@Tag(name = "Complete Matching", description = "확정된 매칭 관련 API 입니다.")
public class CompletedMatchingController {

    private final CompletedMatchingService completedMatchingService;

    @PostMapping
    @Operation(summary = "계약서를 기반으로 매칭을 확정합니다.")
    public ResponseEntity<Void> createCompleteMatching(@RequestParam(name = "contractId") Long contractId) {
        completedMatchingService.createCompletedMatching(contractId);
        return ResponseEntity.ok().build();
    }
}
