package com.becareful.becarefulserver.domain.matching.controller;

import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.matching.dto.response.CaregiverDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.NursingInstitutionRecruitmentStateResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.RecruitmentMatchingStateResponse;
import com.becareful.becarefulserver.domain.matching.service.RecruitmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matching/social-worker")
@Tag(name = "Social Worker Matching", description = "사회복지사가 사용하는 매칭 공고 관련 API 입니다.")
public class SocialWorkerRecruitmentController {

    private final RecruitmentService recruitmentService;

    @Operation(summary = "매칭 공고 등록 (사회복지사 호출)")
    @PostMapping("/recruitment")
    public ResponseEntity<Long> createRecruitment(@Valid @RequestBody RecruitmentCreateRequest request) {
        Long recruitmentId = recruitmentService.createRecruitment(request);
        return ResponseEntity.ok(recruitmentId);
    }

    @Operation(
            summary = "매칭 현황 상세조회 (사회복지사 호출)",
            description = "매칭 현황 데이터의 상세화면을 조회합니다. 매칭된 요양보호사와 지원한 요양보호사 정보가 있습니다.")
    @GetMapping("/recruitment/{recruitmentId}")
    public ResponseEntity<RecruitmentMatchingStateResponse> getMatchingListDetail(@PathVariable Long recruitmentId) {
        RecruitmentMatchingStateResponse recruitmentMatchingStateResponse =
                recruitmentService.getMatchingListDetail(recruitmentId);
        return ResponseEntity.ok(recruitmentMatchingStateResponse);
    }

    @Operation(summary = "매칭 현황 조회 (사회복지사 매칭 현황 조회)")
    @GetMapping("/list")
    public ResponseEntity<List<NursingInstitutionRecruitmentStateResponse>> getMatchingList() {
        List<NursingInstitutionRecruitmentStateResponse> matchingStates = recruitmentService.getMatchingList();
        return ResponseEntity.ok(matchingStates);
    }

    @Operation(summary = "요양보호사 지원 정보 상세 조회 (사회복지사 호출)", description = "요양보호사의 지원 정보를 자세히 조회합니다.")
    @GetMapping("/recruitment/{recruitmentId}/caregiver/{caregiverId}")
    public ResponseEntity<CaregiverDetailResponse> getCaregiverDetailInfo(
            @PathVariable(name = "recruitmentId") Long recruitmentId,
            @PathVariable(name = "caregiverId") Long caregiverId) {
        CaregiverDetailResponse response = recruitmentService.getCaregiverDetailInfo(recruitmentId, caregiverId);
        return ResponseEntity.ok(response);
    }
}
