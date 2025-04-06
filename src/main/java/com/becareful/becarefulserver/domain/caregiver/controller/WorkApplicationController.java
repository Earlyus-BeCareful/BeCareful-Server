package com.becareful.becarefulserver.domain.caregiver.controller;

import com.becareful.becarefulserver.domain.caregiver.dto.request.WorkApplicationUpdateRequest;
import com.becareful.becarefulserver.domain.caregiver.dto.response.WorkApplicationResponse;
import com.becareful.becarefulserver.domain.caregiver.service.WorkApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "WorkApplication", description = "요양보호사 일자리 신청 관련 API 입니다.")
public class WorkApplicationController {

    private final WorkApplicationService workApplicationService;

    @Operation(summary = "일자리 신청 정보 등록/수정")
    @PutMapping("/work-application")
    public ResponseEntity<Void> updateWorkApplication(@Valid @RequestBody WorkApplicationUpdateRequest request) {
        workApplicationService.updateWorkApplication(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "일자리 신청 정보 조회", description = "신청 정보를 등록한 적이 없다면 빈 값을 반환합니다.")
    @GetMapping("/work-application")
    public ResponseEntity<WorkApplicationResponse> getWorkApplication() {
        WorkApplicationResponse response = workApplicationService.getWorkApplication();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "일자리 신청 활성화", description = "등록한 일자리 신청 내용을 기반으로 매칭을 활성화 합니다.")
    @PostMapping("/work-applicatioin/active")
    public ResponseEntity<Void> updateWorkApplicationActive() {
        workApplicationService.updateWorkApplicationActive();
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "일자리 신청 비활성화", description = "일자리 매칭을 비활성화 합니다.")
    @PostMapping("/work-applicatioin/inactive")
    public ResponseEntity<Void> updateWorkApplicationInactive() {
        workApplicationService.updateWorkApplicationInactive();
        return ResponseEntity.ok().build();
    }
}
