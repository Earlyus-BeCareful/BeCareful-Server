package com.becareful.becarefulserver.domain.matching.controller;

import com.becareful.becarefulserver.domain.matching.dto.request.ContractEditRequest;
import com.becareful.becarefulserver.domain.matching.dto.response.ContractDetailResponse;
import com.becareful.becarefulserver.domain.matching.dto.response.ContractInfoListResponse;
import com.becareful.becarefulserver.domain.matching.service.ContractService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contract")
@Tag(name = "Contract", description = "계약서(근무 조건) 관련 API 입니다.")
public class ContractController {

    private final ContractService contractService;

    // 계약서 생성은 사회복지사 controller에 있음

    @Operation(summary = "계약서(근무조건) 리스트", description = "채팅화면에 계약서 반환")
    @GetMapping("/list/{matchingId}")
    public ResponseEntity<ContractInfoListResponse> getContractListAndInfo(@PathVariable Long matchingId) {
        var response = contractService.getContractListAndInfo(matchingId);
        return ResponseEntity.ok(response);
    }

    // 계약서 상세 정보 불러오기(수정할때 사용)
    @Operation(summary = "근무조건 상세 내용 반환", description = "근무조건 수정시 이전 조건 내용을 불러오는데 사용하는 API.")
    @GetMapping("/{contractId}/detail")
    public ResponseEntity<ContractDetailResponse> getContractDetail(@PathVariable Long contractId) {
        var response = contractService.getContractDetail(contractId);
        return ResponseEntity.ok(response);
    }

    // TODO
    // 계약서 수정 내용 저장 - 직전 계약서 필요
    @Operation(summary = "근무조건 수정 내용 저장")
    @PostMapping("/edit")
    public ResponseEntity<Void> editContract(@RequestBody ContractEditRequest request) {
        contractService.editContract(request);
        return ResponseEntity.ok().build();
    }
}
