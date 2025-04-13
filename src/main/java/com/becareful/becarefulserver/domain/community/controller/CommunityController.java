package com.becareful.becarefulserver.domain.community.controller;

import com.becareful.becarefulserver.domain.association.dto.response.AssociationMyResponse;
import com.becareful.becarefulserver.domain.association.service.AssociationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
@Tag(name = "Community", description = "커뮤니티 관련 API 입니다.")
public class CommunityController {

    private final AssociationService associationService;

    @Operation(summary = "커뮤니티 탭 협회 정보 조회", description = "현재 로그인한 사용자가 속한 협회의 정보를 조회합니다.")
    @GetMapping("/my/association")
    public ResponseEntity<AssociationMyResponse> getAssociationInfo() {
        var response = associationService.getMyAssociation();
        return ResponseEntity.ok(response);
    }
}
