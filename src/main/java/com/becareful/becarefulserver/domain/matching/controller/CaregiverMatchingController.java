package com.becareful.becarefulserver.domain.matching.controller;

import com.becareful.becarefulserver.domain.matching.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentMediateRequest;
import com.becareful.becarefulserver.domain.matching.dto.response.CaregiverAppliedMatchingDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.CaregiverAppliedRecruitmentsResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.CaregiverRecruitmentResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.RecruitmentDetailResponse;
import com.becareful.becarefulserver.domain.matching.service.CaregiverMatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matching/caregiver")
@Tag(name = "Caregiver Matching", description = "요양보호사가 사용하는 매칭 공고 관련 API 입니다.")
public class CaregiverMatchingController {

    private final CaregiverMatchingService caregiverMatchingService;

    @Operation(summary = "매칭 공고 리스트 조회 (요양보호사 일자리 리스트 조회)")
    @GetMapping("/list")
    public ResponseEntity<List<CaregiverRecruitmentResponse>> getCaregiverMatchingRecruitmentList() {
        var responses = caregiverMatchingService.getCaregiverMatchingRecruitmentList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "매칭 공고 상세 조회 (요양보호사 일자리 상세 조회)")
    @GetMapping("/recruitment/{recruitmentId}")
    public ResponseEntity<RecruitmentDetailResponse> getRecruitmentDetail(
            @PathVariable("recruitmentId") Long recruitmentId) {
        var response = caregiverMatchingService.getRecruitmentDetail(recruitmentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "매칭 공고 지원 (요양보호사 일자리 지원)")
    @PostMapping("/recruitment/{recruitmentId}/apply")
    public ResponseEntity<Void> applyRecruitment(@PathVariable("recruitmentId") Long recruitmentId) {
        caregiverMatchingService.applyRecruitment(recruitmentId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "근무 조건 조율 (요양보호사 일자리 조율 지원)")
    @PostMapping("/recruitment/{recruitmentId}/mediate")
    public ResponseEntity<Void> mediateMatching(
            @PathVariable("recruitmentId") Long recruitmentId, @RequestBody RecruitmentMediateRequest request) {
        caregiverMatchingService.mediateMatching(recruitmentId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "지원 현황 조회 (요양보호사 나의 지원현황 조회)",
            description = "일자리 지원서가 없거나, 지원 내역이 없다면 빈 리스트를 응답합니다.")
    @GetMapping("/my/recruitment")
    public ResponseEntity<CaregiverAppliedRecruitmentsResponse> getMyRecruitment(
            @RequestParam("matchingStatus") MatchingStatus matchingStatus) {
        var response = caregiverMatchingService.getMyAppliedRecruitment(matchingStatus);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "지원 현황 상세 조회 (요양보호사 나의 지원현황 상세 조회)",
            description = "일자리 지원서가 없거나, 지원 내역이 없다면 빈 리스트를 응답합니다.")
    @GetMapping("/my/recruitment/{recruitmentId}")
    public ResponseEntity<CaregiverAppliedMatchingDetailResponse> getMyRecruitmentDetail(
            @PathVariable("recruitmentId") Long recruitmentId) {
        var response = caregiverMatchingService.getMyAppliedRecruitmentDetail(recruitmentId);
        return ResponseEntity.ok(response);
    }
}
