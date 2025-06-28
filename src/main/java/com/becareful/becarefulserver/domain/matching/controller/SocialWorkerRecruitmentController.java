package com.becareful.becarefulserver.domain.matching.controller;

import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.matching.dto.response.MatchingCaregiverDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.MatchingStatusDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.MatchingStatusSimpleResponse;
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
    public ResponseEntity<MatchingStatusDetailResponse> getMatchingListDetail(@PathVariable Long recruitmentId) {
        var response = recruitmentService.getMatchingDetail(recruitmentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "매칭 현황 조회 (사회복지사 매칭 현황 조회)")
    @GetMapping("/list")
    public ResponseEntity<List<MatchingStatusSimpleResponse>> getMatchingList() {
        var response = recruitmentService.getMatchingList();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 지원 정보 상세 조회 (사회복지사 호출)", description = "요양보호사의 지원 정보를 자세히 조회합니다.")
    @GetMapping("/recruitment/{recruitmentId}/caregiver/{caregiverId}")
    public ResponseEntity<MatchingCaregiverDetailResponse> getCaregiverDetailInfo(
            @PathVariable(name = "recruitmentId") Long recruitmentId,
            @PathVariable(name = "caregiverId") Long caregiverId) {
        var response = recruitmentService.getCaregiverDetailInfo(recruitmentId, caregiverId);
        return ResponseEntity.ok(response);
    }
}
