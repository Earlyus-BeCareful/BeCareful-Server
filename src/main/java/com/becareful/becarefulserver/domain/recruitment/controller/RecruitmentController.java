package com.becareful.becarefulserver.domain.recruitment.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.becareful.becarefulserver.domain.recruitment.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.recruitment.service.RecruitmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitment")
@Tag(name = "Recruitment", description = "매칭 공고 관련 API 입니다.")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;

    @Operation(summary = "매칭 공고 등록 (사회복지사 호출)")
    @PostMapping
    public ResponseEntity<Void> createRecruitment(@Valid @RequestBody RecruitmentCreateRequest request) {
        recruitmentService.createRecruitment(request);
        return ResponseEntity.ok().build();
    }
}
