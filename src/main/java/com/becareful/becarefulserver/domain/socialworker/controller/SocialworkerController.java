package com.becareful.becarefulserver.domain.socialworker.controller;

import com.becareful.becarefulserver.domain.recruitment.dto.response.NursingInstitutionRecruitmentStateResponse;
import com.becareful.becarefulserver.domain.recruitment.dto.response.RecruitmentMatchingStateResponse;
import com.becareful.becarefulserver.domain.recruitment.service.ContractService;
import com.becareful.becarefulserver.domain.recruitment.service.RecruitmentService;
import com.becareful.becarefulserver.domain.socialworker.dto.request.SocialworkerCreateRequest;
import com.becareful.becarefulserver.domain.socialworker.service.SocialworkerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/socialworker")
@Tag(name = "socialworker", description = "사회복지사 관련 API 입니다.")
public class SocialworkerController {

    private final SocialworkerService socialworkerService;
    private final ContractService contractService;
    private final RecruitmentService recruitmentService;

    @Operation(summary = "사회복지사 회원가입", description = "요양기관 ID 필수")
    @PostMapping("/signup")
    public ResponseEntity<Void> createSocialworker(@Valid @RequestBody SocialworkerCreateRequest request){
        Long id = socialworkerService.saveSocialworker(request);
        return ResponseEntity.created(URI.create("/socialworker/" + id)).build();
    }

    //TODO - 채용하기 버튼 누르면 계약서 자동 생성
    @Operation(summary = "채용하기", description = "근무 시작일 선택 후 근무조건 생성")
    @PostMapping("/matching/hire/{matchingId}")
    public ResponseEntity<Void> createContract(@PathVariable("matchingId") Long matchingId, @RequestParam LocalDate workStartDate) {
        contractService.createContract(matchingId, workStartDate);
        return ResponseEntity.ok().build();
    }


    //TODO //매칭현황
    @Operation(summary = "매칭현황", description = "공고별 매칭 현황 리스트 반환")
    @PostMapping("/matching/state")
    public ResponseEntity<List<NursingInstitutionRecruitmentStateResponse>> getMatchingStateByInstitution() {
        List<NursingInstitutionRecruitmentStateResponse> matchingStates = recruitmentService.getMatchingState();
        return ResponseEntity.ok(matchingStates);
    }

    //TODO
    @Operation(summary = "매칭정보", description = "특정 공고의 매칭 상세 정보 반환")
    @GetMapping("/{recruitmentId}")
    public ResponseEntity<RecruitmentMatchingStateResponse> getRecruitmentMatchingState(@PathVariable Long recruitmentId) {
        RecruitmentMatchingStateResponse recruitmentMatchingStateResponse = recruitmentService.getRecruitmentMatchingState(recruitmentId);
        return ResponseEntity.ok(recruitmentMatchingStateResponse);
    }


}
