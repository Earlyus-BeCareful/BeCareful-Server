package com.becareful.becarefulserver.domain.matching.controller;

import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.request.*;
import com.becareful.becarefulserver.domain.matching.dto.response.MatchingCaregiverDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.MatchingStatusDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.SocialWorkerRecruitmentResponse;
import com.becareful.becarefulserver.domain.matching.service.SocialWorkerMatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matching/social-worker")
@Tag(name = "Social Worker Matching", description = "사회복지사가 사용하는 매칭 공고 관련 API 입니다.")
public class SocialWorkerMatchingController {

    private final SocialWorkerMatchingService socialWorkerMatchingService;

    @Operation(summary = "3.1 공고 등록 대기 어르신 리스트 조회 (매칭 대기)")
    @GetMapping("/elderly/waiting")
    public ResponseEntity<Page<ElderlySimpleDto>> getWaitingElderlys(Pageable pageable) {
        var response = socialWorkerMatchingService.getWaitingElderlys(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "3.1 공고 등록 대기 어르신 리스트 검색 (매칭 대기)")
    @PostMapping("/elderly/waiting/search")
    public ResponseEntity<Page<ElderlySimpleDto>> searchWaitingElderlys(
            Pageable pageable, @Valid @RequestBody WaitingMatchingElderlySearchRequest request) {
        var response = socialWorkerMatchingService.searchWaitingElderlys(pageable, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "3.1 공고 목록 조회 (매칭중 / 매칭완료)")
    @GetMapping("/recruitment")
    public ResponseEntity<Page<SocialWorkerRecruitmentResponse>> getMatchingList(
            @RequestParam ElderlyMatchingStatusFilter elderlyMatchingStatusFilter, Pageable pageable) {
        var response = socialWorkerMatchingService.getRecruitmentList(elderlyMatchingStatusFilter, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "3.1 공고 목록 검색 (매칭중 / 매칭완료)")
    @PostMapping("/recruitment/search")
    public ResponseEntity<Page<SocialWorkerRecruitmentResponse>> searchMatchingList(
            @RequestParam ElderlyMatchingStatusFilter elderlyMatchingStatusFilter,
            Pageable pageable,
            @Valid @RequestBody MatchingRecruitmentSearchRequest request) {
        var response =
                socialWorkerMatchingService.searchRecruitmentList(elderlyMatchingStatusFilter, pageable, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "3.2.1 매칭 공고 등록", description = "3.2.1.3 화면에서 사용하는 매칭 공고 등록 API")
    @PostMapping("/recruitment")
    public ResponseEntity<Void> createRecruitment(@Valid @RequestBody RecruitmentCreateRequest request) {
        Long recruitmentId = socialWorkerMatchingService.createRecruitment(request);
        return ResponseEntity.created(URI.create("/matching/social-worker/recruitment/" + recruitmentId))
                .build();
    }

    @Operation(summary = "3.2.1 매칭 공고 수정", description = "3.2.1.3 화면에서 사용하는 매칭 공고 수정 API")
    @PutMapping("/recruitment/{recruitmentId}")
    public ResponseEntity<Void> updateRecruitment(
            @PathVariable Long recruitmentId, @Valid @RequestBody RecruitmentUpdateRequest request) {
        socialWorkerMatchingService.updateRecruitment(recruitmentId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "3.2.1 매칭 공고 검증", description = "3.2.1.3 화면에서 매칭 공고 등록시 일정 중복을 검증하는 API, 검증에 실패하면 에러 발생")
    @PostMapping("/recruitment/validate-duplicated")
    public ResponseEntity<Void> validateRecruitmentDuplicated(
            @Valid @RequestBody RecruitmentValidateDuplicatedRequest request) {
        socialWorkerMatchingService.validateDuplicated(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "매칭 현황 상세 조회", description = "매칭 현황 데이터의 상세화면을 조회합니다. 매칭된 요양보호사와 지원한 요양보호사 정보가 있습니다.")
    @GetMapping("/recruitment/{recruitmentId}")
    public ResponseEntity<MatchingStatusDetailResponse> getMatchingListDetail(@PathVariable Long recruitmentId) {
        var response = socialWorkerMatchingService.getMatchingDetail(recruitmentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 지원 정보 상세 조회", description = "요양보호사의 지원 정보를 자세히 조회합니다.")
    @GetMapping("/recruitment/{recruitmentId}/caregiver/{caregiverId}")
    public ResponseEntity<MatchingCaregiverDetailResponse> getCaregiverDetailInfo(
            @PathVariable(name = "recruitmentId") Long recruitmentId,
            @PathVariable(name = "caregiverId") Long caregiverId) {
        var response = socialWorkerMatchingService.getCaregiverDetailInfo(recruitmentId, caregiverId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사에게 근무 제안하기", description = "근무 시작일 선택 후 채팅방 생성")
    @PostMapping("/{matchingId}/propose")
    public ResponseEntity<Void> proposeCaregiver(
            @PathVariable("matchingId") Long matchingId, @RequestParam LocalDate workStartDate) {
        socialWorkerMatchingService.propose(matchingId, workStartDate);
        return ResponseEntity.ok().build();
    }
}
