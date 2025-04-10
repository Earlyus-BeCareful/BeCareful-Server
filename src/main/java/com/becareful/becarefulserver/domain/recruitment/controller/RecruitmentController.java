package com.becareful.becarefulserver.domain.recruitment.controller;

import com.becareful.becarefulserver.domain.recruitment.domain.MatchingStatus;
import com.becareful.becarefulserver.domain.recruitment.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.recruitment.dto.request.RecruitmentMediateRequest;
import com.becareful.becarefulserver.domain.recruitment.dto.response.*;
import com.becareful.becarefulserver.domain.recruitment.repository.MatchingRepository;
import com.becareful.becarefulserver.domain.recruitment.service.RecruitmentService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Hidden
@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitment")
@Tag(name = "Recruitment", description = "매칭 공고 관련 API 입니다.")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;
    private final MatchingRepository matchingRepository;

    @Operation(summary = "매칭 공고 등록 (사회복지사 호출)")
    @PostMapping
    public ResponseEntity<Long> createRecruitment(@Valid @RequestBody RecruitmentCreateRequest request) {
        Long recruitmentId = recruitmentService.createRecruitment(request);
        return ResponseEntity.ok(recruitmentId);
    }

    @Operation(summary = "매칭 현황 상세조회 (사회복지사 호출)", description = "매칭 현황 데이터의 상세화면을 조회합니다. 매칭된 요양보호사와 지원한 요양보호사 정보가 있습니다.")
    @GetMapping("/{recruitmentId}")
    public ResponseEntity<RecruitmentMatchingStateResponse> getMatchingListDetail(@PathVariable Long recruitmentId) {
        RecruitmentMatchingStateResponse recruitmentMatchingStateResponse = recruitmentService.getMatchingListDetail(recruitmentId);
        return ResponseEntity.ok(recruitmentMatchingStateResponse);
    }

    @Operation(summary = "매칭 공고 리스트 조회 (요양보호사 일자리 리스트 조회)")
    @GetMapping("/list")
    public ResponseEntity<List<RecruitmentResponse>> getRecruitmentList() {
        List<RecruitmentResponse> responses = recruitmentService.getRecruitmentList();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "매칭 현황 조회 (사회복지사 매칭 현황 조회)")
    @GetMapping("/matching-list")
    public ResponseEntity<List<NursingInstitutionRecruitmentStateResponse>> getMatchingList() {
        List<NursingInstitutionRecruitmentStateResponse> matchingStates = recruitmentService.getMatchingList();
        return ResponseEntity.ok(matchingStates);
    }

    @Operation(summary = "매칭 공고 상세 조회 (요양보호사 일자리 상세 조회)")
    @GetMapping("/list/{recruitmentId}")
    public ResponseEntity<RecruitmentDetailResponse> getRecruitmentDetail(@PathVariable("recruitmentId") Long recruitmentId) {
        RecruitmentDetailResponse response = recruitmentService.getRecruitmentDetail(recruitmentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "매칭 공고 지원 (요양보호사 일자리 지원)")
    @PostMapping("/{recruitmentId}/apply")
    public ResponseEntity<Void> applyRecruitment(@PathVariable("recruitmentId") Long recruitmentId) {
        recruitmentService.applyRecruitment(recruitmentId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "매칭 공고 거절 (요양보호사 일자리 거절)")
    @PostMapping("/{recruitmentId}/reject")
    public ResponseEntity<Void> rejectMatching(@PathVariable("recruitmentId") Long recruitmentId) {
        recruitmentService.rejectMatching(recruitmentId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "근무 조건 조율 (요양보호사 일자리 조율 지원)")
    @PostMapping("/{recruitmentId}/mediate")
    public ResponseEntity<Void> mediateMatching(@PathVariable("recruitmentId") Long recruitmentId, @RequestBody RecruitmentMediateRequest request) {
        recruitmentService.mediateMatching(recruitmentId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "지원 현황 조회 (요양보호사 나의 지원현황 조회)", description = "일자리 신청서가 없거나, 지원 내역이 없다면 빈 리스트를 응답합니다. '거절'은 요양보호사가 지원 거절한 경우이므로, 관리자가 거절한 경우에는 '불합격' 상태로 조회해야 합니다.")
    @GetMapping("/my/recruitment")
    public ResponseEntity<List<MyRecruitmentResponse>> getMyRecruitment(@RequestParam("recruitmentStatus") MatchingStatus matchingStatus) {
        List<MyRecruitmentResponse> response = recruitmentService.getMyRecruitment(matchingStatus);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "지원 현황 상세 조회 (요양보호사 나의 지원현황 상세 조회)", description = "일자리 신청서가 없거나, 지원 내역이 없다면 빈 리스트를 응답합니다. '거절'은 요양보호사가 지원 거절한 경우이므로, 관리자가 거절한 경우에는 '불합격' 상태로 조회해야 합니다.")
    @GetMapping("/my/recruitment/{recruitmentId}")
    public ResponseEntity<MyRecruitmentDetailResponse> getMyRecruitmentDetail(@PathVariable("recruitmentId") Long recruitmentId) {
        MyRecruitmentDetailResponse response = recruitmentService.getMyRecruitmentDetail(recruitmentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 지원 정보 상세 조회 (사회복지사 호출)", description = "요양보호사의 지원 정보를 자세히 조회합니다.")
    @GetMapping("/{recruitmentId}/caregiver/{caregiverId}")
    public ResponseEntity<CaregiverDetailResponse> getCaregiverDetailInfo(@PathVariable(name = "recruitmentId") Long recruitmentId, @PathVariable(name = "caregiverId") Long caregiverId) {
        CaregiverDetailResponse response = recruitmentService.getCaregiverDetailInfo(recruitmentId, caregiverId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<Long> convertMatchingIdToRecruitmentId(@PathParam("matchingId") Long matchingId) {
        Long recruitmentId = matchingRepository.findById(matchingId)
                .map(matching -> matching.getRecruitment().getId())
                .orElseThrow(() -> new RuntimeException("변환실패"));
        return ResponseEntity.ok(recruitmentId);
    }
}
