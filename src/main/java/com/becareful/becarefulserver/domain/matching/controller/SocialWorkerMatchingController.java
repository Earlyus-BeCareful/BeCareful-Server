package com.becareful.becarefulserver.domain.matching.controller;

import com.becareful.becarefulserver.domain.matching.dto.ElderlySimpleDto;
import com.becareful.becarefulserver.domain.matching.dto.RecruitmentDto;
import com.becareful.becarefulserver.domain.matching.dto.request.*;
import com.becareful.becarefulserver.domain.matching.dto.response.MatchingCaregiverDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.RecruitmentMatchingStatusResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.SocialWorkerRecruitmentResponse;
import com.becareful.becarefulserver.domain.matching.service.SocialWorkerMatchingService;
import io.swagger.v3.oas.annotations.*;
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

    @Operation(summary = "3.1.4 공고 상세 조회", description = "공고 상세 화면을 조회합니다.")
    @GetMapping("/recruitment/{recruitmentId}")
    public ResponseEntity<RecruitmentDto> getRecruitmentDetail(@PathVariable Long recruitmentId) {
        var response = socialWorkerMatchingService.getRecruitment(recruitmentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "3.1.4 공고 수정", description = "공고 내용을 수정합니다. 지원자가 없을 때만 수정 가능합니다.")
    @PutMapping("/recruitment/{recruitmentId}")
    public ResponseEntity<Void> updateRecruitmentDetail(
            @PathVariable Long recruitmentId, @Valid @RequestBody RecruitmentUpdateRequest request) {
        socialWorkerMatchingService.updateRecruitment(recruitmentId, request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "3.1.4 공고 삭제", description = "공고를 삭제합니다. 지원자가 없을 때만 삭제 가능합니다.")
    @DeleteMapping("/recruitment/{recruitmentId}")
    public ResponseEntity<Void> deleteRecruitmentDetail(@PathVariable Long recruitmentId) {
        socialWorkerMatchingService.deleteRecruitment(recruitmentId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "3.1.4 공고 마감 처리", description = "공고를 마감하고, 신규 매칭 및 지원을 더 이상 받지 않습니다.")
    @PostMapping("/recruitment/{recruitmentId}/close")
    public ResponseEntity<Void> closeRecruitment(@PathVariable Long recruitmentId) {
        socialWorkerMatchingService.closeRecruitment(recruitmentId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "3.1.4 공고 매칭 현황 조회", description = "공고의 요양보호사 매칭 및 지원 현황을 조회합니다.")
    @GetMapping("/recruitment/{recruitmentId}/matching-status")
    public ResponseEntity<RecruitmentMatchingStatusResponse> getRecruitmentMatchingStatus(
            @PathVariable Long recruitmentId) {
        var response = socialWorkerMatchingService.getMatchingStatus(recruitmentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "3.1.4 요양보호사 지원 정보 상세 조회", description = "매칭된 요양보호사 상세 정보를 조회합니다.")
    @GetMapping("/recruitment/{recruitmentId}/caregiver/{caregiverId}")
    public ResponseEntity<MatchingCaregiverDetailResponse> getCaregiverDetailInfo(
            @PathVariable Long recruitmentId, @PathVariable Long caregiverId) {
        var response = socialWorkerMatchingService.getCaregiverDetailInfo(recruitmentId, caregiverId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "3.1.4 요양보호사에게 근무 제안", description = "근무 시작일을 지정하면 해당 요양보호사에게 근무 제안을 보내며,제안과 동시에 채팅방이 생성됩니다.")
    @PostMapping("/recruitment/{recruitmentId}/caregiver/{caregiverId}/propose")
    public ResponseEntity<Long> proposeCaregiver(
            @PathVariable Long recruitmentId, @PathVariable Long caregiverId, @RequestParam LocalDate workStartDate) {
        Long chatRoomId = socialWorkerMatchingService.proposeWork(recruitmentId, caregiverId, workStartDate);
        return ResponseEntity.ok(chatRoomId);
    }

    @Operation(summary = "3.2.1 매칭 공고 등록", description = "3.2.1.3 화면에서 사용하는 매칭 공고 등록 API")
    @PostMapping("/recruitment")
    public ResponseEntity<Void> createRecruitment(@Valid @RequestBody RecruitmentCreateRequest request) {
        Long recruitmentId = socialWorkerMatchingService.createRecruitment(request);
        return ResponseEntity.created(URI.create("/matching/social-worker/recruitment/" + recruitmentId))
                .build();
    }

    @Operation(summary = "3.2.1 매칭 공고 검증", description = "3.2.1.3 화면에서 매칭 공고 등록시 일정 중복을 검증하는 API, 검증에 실패하면 에러 발생")
    @PostMapping("/recruitment/validate-duplicated")
    public ResponseEntity<Void> validateRecruitmentDuplicated(
            @Valid @RequestBody RecruitmentValidateDuplicatedRequest request) {
        socialWorkerMatchingService.validateDuplicated(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "3.6 지원리스트(지원 현황) - 매칭 보류")
    @PatchMapping("/{applicationId}/pending")
    public ResponseEntity<Void> setPending(@Parameter Long applicationId) {
        socialWorkerMatchingService.postponeApplicationDecision(applicationId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "3.6 지원리스트(지원 현황) - 매칭 보류 취소",
            description = "매칭 보류 상태에서 [근무 제안하기] 버튼 클릭시 보류하기 취소 모달 - 보류 취소하기")
    @PatchMapping("/{applicationId}/pending/cancel")
    public ResponseEntity<Void> unsetPending(@Parameter Long applicationId) {
        socialWorkerMatchingService.resumeApplicationDecision(applicationId);
        return ResponseEntity.ok().build();
    }
}
