package com.becareful.becarefulserver.domain.association.controller;

import com.becareful.becarefulserver.domain.association.dto.request.AssociationJoinRequest;
import com.becareful.becarefulserver.domain.association.dto.response.AssociationJoinApplicationListResponse;
import com.becareful.becarefulserver.domain.association.service.AssociationJoinService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/association/join-requests")
public class AssociationJoinController {

    private final AssociationJoinService associationJoinService;

    @Operation(summary = "협회 가입 신청", description = "협회 임원진, 회원 전용 API")
    @PostMapping
    public ResponseEntity<Void> joinAssociation(@Valid @RequestBody AssociationJoinRequest request) {
        associationJoinService.applyJoinAssociation(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "협회 가입 신청 취소", description = "본인의 협회 가입 신청을 취소하는 API")
    @DeleteMapping
    public ResponseEntity<Void> cancelMyJoinApplication() {
        associationJoinService.cancelMyJoinApplication();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "협회 가입 신청 목록 보기")
    @GetMapping
    public ResponseEntity<AssociationJoinApplicationListResponse> getPendingJoinApplications() {
        var response = associationJoinService.getAssociationJoinApplicationList();
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "협회 가입 신청 승인", description = "협회장만 접근 가능한 API")
    @PutMapping("/{applicationId}/accept")
    public ResponseEntity<Void> acceptAssociationJoinApplication(@PathVariable Long applicationId) {
        associationJoinService.acceptJoinAssociation(applicationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "협회 가입 신청 반려", description = "협회장만 접근 가능한 API")
    @PutMapping("/{applicationId}/reject") // TODO : method put -> post
    public ResponseEntity<Void> rejectAssociationJoinApplication(@PathVariable Long applicationId) {
        associationJoinService.rejectJoinAssociation(applicationId);
        return ResponseEntity.ok().build();
    }
}
