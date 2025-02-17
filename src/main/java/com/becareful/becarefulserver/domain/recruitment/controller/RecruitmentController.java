package com.becareful.becarefulserver.domain.recruitment.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.becareful.becarefulserver.domain.recruitment.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.recruitment.dto.response.RecruitmentDetailResponse;
import com.becareful.becarefulserver.domain.recruitment.dto.response.RecruitmentResponse;
import com.becareful.becarefulserver.domain.recruitment.service.RecruitmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
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

    @Operation(summary = "매칭 공고 리스트 조회 (요양보호사 일자리 리스트 조회)")
    @GetMapping("/list")
    public ResponseEntity<List<RecruitmentResponse>> getRecruitmentList() {
        List<RecruitmentResponse> responses = recruitmentService.getRecruitmentList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "매칭 공고 상세 조회 (요양보호사 일자리 상세 조회)")
    @GetMapping("/list/{recruitmentId}")
    public ResponseEntity<RecruitmentDetailResponse> getRecruitmentDetail(@PathVariable("recruitmentId") Long recruitmentId) {
        RecruitmentDetailResponse response = recruitmentService.getRecruitmentDetail(recruitmentId);
        return ResponseEntity.ok(response);
    }
}
