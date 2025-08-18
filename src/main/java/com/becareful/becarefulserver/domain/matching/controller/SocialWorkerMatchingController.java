package com.becareful.becarefulserver.domain.matching.controller;

import com.becareful.becarefulserver.domain.matching.domain.MatchingStatusFilter;
import com.becareful.becarefulserver.domain.matching.dto.request.RecruitmentCreateRequest;
import com.becareful.becarefulserver.domain.matching.dto.response.MatchingCaregiverDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.MatchingStatusDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.MatchingStatusSimpleResponse;
import com.becareful.becarefulserver.domain.matching.service.ContractService;
import com.becareful.becarefulserver.domain.matching.service.MatchingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/matching/social-worker")
@Tag(name = "Social Worker Matching", description = "사회복지사가 사용하는 매칭 공고 관련 API 입니다.")
public class SocialWorkerMatchingController {

    private final MatchingService matchingService;
    private final ContractService contractService;

    @Operation(summary = "매칭 공고 등록")
    @PostMapping("/recruitment")
    public ResponseEntity<Long> createRecruitment(@Valid @RequestBody RecruitmentCreateRequest request) {
        Long recruitmentId = matchingService.createRecruitment(request);
        return ResponseEntity.ok(recruitmentId); // TODO : Created 응답으로 변경
    }

    @Operation(summary = "매칭 현황 조회")
    @GetMapping("/list")
    public ResponseEntity<List<MatchingStatusSimpleResponse>> getMatchingList(
            @RequestParam MatchingStatusFilter matchingStatusFilter) {
        var response = matchingService.getMatchingList(matchingStatusFilter);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "매칭 현황 상세 조회", description = "매칭 현황 데이터의 상세화면을 조회합니다. 매칭된 요양보호사와 지원한 요양보호사 정보가 있습니다.")
    @GetMapping("/recruitment/{recruitmentId}")
    public ResponseEntity<MatchingStatusDetailResponse> getMatchingListDetail(@PathVariable Long recruitmentId) {
        var response = matchingService.getMatchingDetail(recruitmentId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 지원 정보 상세 조회", description = "요양보호사의 지원 정보를 자세히 조회합니다.")
    @GetMapping("/recruitment/{recruitmentId}/caregiver/{caregiverId}")
    public ResponseEntity<MatchingCaregiverDetailResponse> getCaregiverDetailInfo(
            @PathVariable(name = "recruitmentId") Long recruitmentId,
            @PathVariable(name = "caregiverId") Long caregiverId) {
        var response = matchingService.getCaregiverDetailInfo(recruitmentId, caregiverId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "요양보호사 채용하기", description = "근무 시작일 선택 후 계약서 생성")
    @PostMapping("/{matchingId}/hire")
    public ResponseEntity<Void> createContract(
            @PathVariable("matchingId") Long matchingId, @RequestParam LocalDate workStartDate) {
        contractService.createContract(matchingId, workStartDate);
        return ResponseEntity.ok().build();
    }
}
